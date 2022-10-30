package com.fthiery.catalog.datasources.wikiparser

const val TAGS = """(\{\{)|(\}\})|(<.*?>)|(\[\[)|(]])"""

enum class FontStyle {
    Normal, Italic, Bold, ItalicBold
}

interface Node {
    var style: FontStyle
    override fun toString(): String
}

abstract class ParentNode : Node, ArrayList<Node>() {
    abstract override fun toString(): String

    override var style: FontStyle
        get() = _style
        set(value) {
            _style = value
            forEach { it.style = value }
        }
    private var _style: FontStyle = FontStyle.Normal

    fun getProperties(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        forEach {
            if (it is ParentNode) map.putAll(it.getProperties())
        }
        return map
    }
}

class RootNode(
) : ParentNode() {
    override fun toString(): String = joinToString("")
}

class TextNode(
    var text: String = "",
    override var style: FontStyle = FontStyle.Normal
) : Node {
    override fun toString(): String = text
}

class TitleNode(
    var text: String = "",
    var level: Int = 2,
    override var style: FontStyle = FontStyle.Normal
) : Node {
    override fun toString(): String = text
}

class LinkNode(
    var label: String = "",
    var target: String = "",
    override var style: FontStyle = FontStyle.Normal
) : Node {
    override fun toString(): String = label
}

class PropertyNode(
    var key: String
) : ParentNode() {
    override fun toString(): String = "$key = $value"

    val value: String
        get() = joinToString("").trim()
}

class HtmlNode(
    var tag: String = ""
) : ParentNode() {
    override fun toString(): String {
        return when (tag.lowercase()) {
            "span" -> joinToString("")
            "ref"  -> tag
            else   -> ""
        }
    }
}

class TemplateNode(
    var name: String = ""
) : ParentNode() {
    override fun toString(): String {
        return when (name.lowercase()) {
            "langue"           -> last().toString()
            "unité"            -> joinToString(" ")
            "nihongo foot"     -> first().toString()
            "vgrelease"        -> {
                val stringBuilder = StringBuilder()
                for (i in this.indices step 2) {
                    val key = this[i].toString()
                    val value = getOrNull(i + 1).toString()
                    stringBuilder.append("$key: $value, ")
                }
                stringBuilder.toString().trim()
            }
            "collapsible list" -> first().toString()
            else               -> name
        }
    }
}

class ArgumentNode(
    var key: String = ""
) : ParentNode() {
    override fun toString(): String {
        return when (key) {
            "", "title" -> value
            else        -> "$key = $value"
        }
    }

    val value: String
        get() = joinToString("").trim()
}