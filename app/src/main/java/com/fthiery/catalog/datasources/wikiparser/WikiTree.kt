package com.fthiery.catalog.datasources.wikiparser

enum class FontStyle {
    Normal, Italic, Bold, ItalicBold
}

val MONTHS = listOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
)

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

    open fun getProperties(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        forEach {
            if (it is ParentNode) {
                val properties = it.getProperties()
                if (it !is TemplateNode || it.name.lowercase().startsWith("infobox"))
                    map.putAll(properties)
            }
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

class HtmlNode(
    var tag: String = ""
) : ParentNode() {
    override fun toString(): String {
        return when (tag.lowercase()) {
            "span" -> joinToString("")
            "br"   -> "\n"
            else   -> ""
        }
    }
}

class TemplateNode(
    var name: String = ""
) : ParentNode() {
    override fun toString(): String {
        return when (name.lowercase()) {
            "nihongo", "nihongo foot", "collapsible list"
                 -> first().toString()
            "unbulleted list", "ubl", "plainlist", "based on"
                 -> joinToString("\n")
            "vgrelease", "video game release"
                 -> {
                val stringBuilder = StringBuilder()
                stringBuilder.append("\n")
                for (i in this.indices step 2) {
                    val key = this[i].toString()
                    val value = getOrNull(i + 1).toString()
                    stringBuilder.append("$key: $value\n")
                }
                stringBuilder.append("\n")
                stringBuilder.toString()
            }
            else -> {
                when {
                    name.lowercase().startsWith("date range")
                         -> {
                        val stringBuilder = StringBuilder()
                        for (i in this.indices step 3) {
                            val year = getOrNull(i).toString()
                            val month = getOrNull(i + 1).toString()
                            val strMonth = MONTHS.getOrElse(month.toIntOrNull() ?: 12) { month }
                            val day = getOrNull(i + 2).toString()
                            stringBuilder.append("$strMonth $day, $year")
                            if (i < this.size - 3) stringBuilder.append(" – ")
                        }
                        stringBuilder.toString()
                    }
                    else -> ""
                }
            }
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

    override fun getProperties(): Map<String, String> {
        return if (value.isNotEmpty())
            mapOf(key to value)
        else mapOf()
    }
}