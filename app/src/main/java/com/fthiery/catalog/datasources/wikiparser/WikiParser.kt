package com.fthiery.catalog.datasources.wikiparser

class WikiParser(
    private val type: WikiType = WikiType.Root,
    var tag: String = ""
) : ArrayList<Node>() {

    private var remainingText = ""

    companion object {
        const val FIRST_PASS = """(''+.*?''+)|(==+.*?==+)|(\{\{)|(\}\})|(\[+)|(]+)|(\|)|(<.*?>)"""
    }

    enum class WikiType {
        Root, Text, Title, Link, Html, Template, Argument
    }

    override fun toString(): String = joinToString(separator = "")

    fun parse(textToParse: String): Node {
        remainingText = firstPass(textToParse.clean())
        secondPass()

        val node: Node = when (type) {
            WikiType.Root     -> RootNode()
            WikiType.Text     -> TextNode()
            WikiType.Title    -> TitleNode()
            WikiType.Link     -> LinkNode(last().toString(), first().toString())
            WikiType.Html     -> HtmlNode(tag)
            WikiType.Template -> {
                val name = first().toString().trim()
                removeFirst()
                TemplateNode(name)
            }
            WikiType.Argument -> ArgumentNode()
        }
        if (node is ParentNode) node.addAll(this)
        return node
    }

    private fun String.clean(): String {
        return this.removeQuotes().removeComments().removeNBSP()
    }

    private fun String.removeComments(): String {
        return Regex("""<!--.+?-->""").replace(this, "")
    }

    private fun String.removeQuotes(): String {
        // Ignores Bold and Italic text to circumvent a bug
        // Should instead use SpannableString for textNodes and parse Bold and Italic in the second pass
        return Regex("""\'\'+""").replace(this,"")
    }

    private fun String.removeNBSP(): String {
        return this.replace("&nbsp;"," ")
    }

    /**
     * Parses the text, finds wiki templates and creates nodes
     */
    private fun firstPass(textToParse: String): String {
        var text = textToParse
        val regex = Regex(FIRST_PASS)
        // Parse the text until the end
        do {
            val result = regex.find(text)
            val split = text.split(regex, 2)

            when {
                result == null                                       -> {
                    // Last Text Node
                    add(TextNode(text))
                    text = ""
                }
                result.range.first != 0                              -> {
                    // Text Node
                    val node = TextNode(split.first())
                    add(node)
                    text = text.substring(result.range.first)
                }
                result.value == "|"                                  -> {
                    text = when (type) {
                        WikiType.Argument -> return text
                        WikiType.Template -> {
                            val parser = WikiParser(WikiType.Argument)
                            add(parser.parse(split.last()))
                            parser.remainingText
                        }
                        else              -> split.last()
                    }
                }
                result.value.startsWith("\'\'")                      -> {
                    // Italic or bold text
                    val content = result.value.trim('\'')
                    val node = WikiParser().parse(content) as RootNode
                    node.style = italicBold((result.value.length - content.length) / 2)
                    addAll(node)
                    text = split.last()
                }
                result.value.startsWith("==")                        -> {
                    // Title Node
                    val content = result.value.trim('=')
                    val level = (result.value.length - content.length) / 2
                    add(TitleNode(content.trim(), level))
                    text = split.last()
                }
                result.value == "{{"                                 -> {
                    // Template Node
                    // When an opening tag is found, parses the text after it
                    // and gets the result as a list of nodes and the text remaining after the closing tag
                    val parser = WikiParser(WikiType.Template)
                    // Adds this node to our list of nodes
                    add(parser.parse(split.last()))
                    // Continues parsing the text after the closing tag
                    text = parser.remainingText
                }
                result.value.startsWith("[")                         -> {
                    val parser = WikiParser(WikiType.Link)
                    add(parser.parse(split.last()))
                    text = parser.remainingText
                }
                result.value.startsWith("<")                         -> {
                    // HTML Tag
                    val match = Regex("""<(/?)(.*?)(\s.*?)?(/?)>""")
                        .find(result.value)
                        ?.groupValues
                        ?: listOf("", "", "", "", "")
                    when {
                        match[1] == "/" -> {
                            // Closing tag
                            return split.last()
                        }
                        match.last() == "/" || match[2].startsWith("br")
                                        -> {
                            // Unique tag
                            add(HtmlNode(tag = match[2].trim()))
                            text = split.last()
                        }
                        else            -> {
                            // Opening tag
                            // TODO: Ignores properties
                            val parser = WikiParser(type = WikiType.Html, tag = match[2].trim())
                            add(parser.parse(split.last()))
                            text = parser.remainingText
                        }
                    }
                }
                result.value == "}}" || result.value.startsWith("]") -> {
                    // When a closing tag is found,
                    // return the list of nodes and the text remaining after the tag
                    if (type != WikiType.Argument) text = split.last()
                    return text
                }
            }
        } while (text.isNotEmpty())
        return ""
    }

    /**
     * Parses the nodes to find template arguments
     */
    private fun secondPass() {
        val regex = Regex(""".*=""")
        forEach {
            if (it is ArgumentNode && it.isNotEmpty()) {
                val text = it.first().toString()
                val result = regex.find(text)
                if (result != null) {
                    val split = text.split('=', limit = 2)
                    it.key = split.first().trim()
                    val value = split.last().trim()

                    if (value.isNotEmpty()) it.add(1, TextNode(value, it.first().style))

                    it.removeFirst()
                }
            }
        }
    }

    private fun italicBold(len: Int): FontStyle = when (len) {
        2    -> FontStyle.Italic
        3    -> FontStyle.Bold
        5    -> FontStyle.ItalicBold
        else -> FontStyle.Normal
    }
}