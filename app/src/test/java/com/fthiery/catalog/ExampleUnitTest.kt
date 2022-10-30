package com.fthiery.catalog


import com.fthiery.catalog.datasources.wikiparser.RootNode
import com.fthiery.catalog.datasources.wikiparser.WikiParser
import com.fthiery.catalog.datasources.wikiparser.WikiTextParser
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class WikiTest {
    @Test
    fun test_wikitext_parser() {
        val result = WikiTextParser(wikiText).parse()
        return
    }

    @Test
    fun test_wikiParser() {
        val result = WikiParser().parse(wikiText)
        return
    }
}