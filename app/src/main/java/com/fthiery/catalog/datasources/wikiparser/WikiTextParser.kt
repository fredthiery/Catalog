package com.fthiery.catalog.datasources.wikiparser

const val PATTERN = """(''+.*?''+)|(==+ .*? ==+)|(\{\{)|(\}\})|(<.*?>)|(\[\[)|(]])|(\|)"""
// TODO: Gérer les paragraphes

enum class WikiType {
    Root, Text, Title, Link, Property, Html, Template
}

class WikiTextParser(
    var text: String,
    var type: WikiType = WikiType.Root
) {
    val nodes: MutableList<Node> = mutableListOf()

    override fun toString(): String = nodes.joinToString(separator = "")

    fun parseStructure() {
        // First pass: Start by finding all the main tags and populating corresponding nodes
        // Second pass: run through each node and find arguments, properties, etc.
    }

    fun parse(nodesToParse: Int? = null): WikiTextParser {
        // Remove comments
        text = Regex("""<!--.+?-->""").replace(text, "")

        // Parse the text until the end
        do {
            val result = Regex(PATTERN).find(text)

            val startOfTag = result?.range?.first ?: text.length
            val afterTag = (result?.range?.last ?: text.length) + 1

            when {
                result?.range?.first != 0                    -> {
                    // Text Node
                    // TODO: Quand dans Template, créer argument node après chaque |
                    val node = TextNode(text.substring(0, startOfTag))
                    nodes.add(node)
                    text = text.substring(startOfTag)
                }
                result.value.startsWith("\'\'")              -> {
                    // Italic or bold text
                    val match = Regex("""(''+)(.*?)(''+)""").find(result.value)
                    match?.let {
                        val resultNodes = WikiTextParser(
                            text = match.groupValues[2],
                            type = WikiType.Text
                        ).parse().nodes
                        val (italic, bold) = italicBold(match.groupValues[1])
                        nodes.addAll(resultNodes)
                    }
                    text = text.substring(afterTag)
                }
                result.value.startsWith("==")                -> {
                    // Title Node
                    val title = result.groupValues[2]
                    val match = Regex("""(==+) (.*?) (==+)""").find(title)
                    match?.let {
                        nodes.add(TitleNode(match.groupValues[2], match.groupValues[1].length))
                    }
                    text = text.substring(afterTag)
                }
                result.value == "{{"                         -> {
                    // Template Node
                    // When an opening tag is found, parses the text after it
                    // and gets the result as a list of nodes and the text remaining after the closing tag
                    val wiki = WikiTextParser(
                        text = text.substring(afterTag),
                        type = WikiType.Template
                    ).parse()
                    // Creates a node whose name is the content of the first child returned
                    val node = TemplateNode(name = wiki.nodes.first().toString())
                    // Adds the other children to this node
                    node.addAll(wiki.nodes.drop(1))
                    // Adds this node to our list of nodes
                    nodes.add(node)
                    // Continues parsing the text after the closing tag
                    text = wiki.text
                }
                result.value.startsWith("<")                 -> {
                    // HTML Tag
                    val match = Regex("""<(/?)(.*?)(\s.*?)?(/?)>""")
                        .find(result.value)
                        ?.groupValues
                        ?: listOf("", "", "", "", "")
                    when {
                        match[1] == "/" -> {
                            // Closing tag
                            text = text.substring(afterTag)
                            return this
                        }
                        match.last() == "/"
                                || match[2].startsWith("br")
                                        -> {
                            // Unique tag
                            nodes.add(HtmlNode(tag = match[2].trim()))
                            text = text.substring(afterTag)
                        }
                        else            -> {
                            // Opening tag
                            // TODO: Ignores properties
                            val wiki = WikiTextParser(
                                text = text.substring(afterTag),
                                type = WikiType.Html
                            ).parse()
                            val node = HtmlNode(tag = match[2].trim())
                            node.addAll(wiki.nodes)
                            nodes.add(node)
                            text = wiki.text
                        }
                    }
                }
                result.value == "[["                         -> {
                    // Opening Link
                    val wiki = WikiTextParser(
                        text = text.substring(afterTag),
                        type = WikiType.Link
                    ).parse()
                    val node = LinkNode(
                        label = wiki.nodes.last().toString(),
                        target = wiki.nodes.first().toString()
                    )
                    nodes.add(node)
                    text = wiki.text
                }
                result.value == "}}" || result.value == "]]" -> {
                    // When a closing tag is found,
                    // return the list of nodes and the text remaining after the tag
                    text = text.substring(afterTag)
                    return this
                }
                result.value == "|"                          -> {
                    text = text.substring(afterTag)
                }
            }
        } while (
            text.isNotEmpty()
            && if (nodesToParse != null) nodes.size < nodesToParse else true
        )
        return this
    }

    private fun italicBold(string: String): Pair<Boolean, Boolean> = when (string.length) {
        2    -> true to false
        3    -> false to true
        5    -> true to true
        else -> false to false
    }
}