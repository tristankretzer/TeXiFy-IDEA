package nl.hannahsten.texifyidea.index.file

import nl.hannahsten.texifyidea.lang.LatexRegularCommand

/**
 * Do some basic formatting on documentation strings found in dtx files.
 * Only done when indexing, but it should still be fast because it can be done up to 28714 times for full TeX Live.
 * Should format to valid HTML as used in the docs popup.
 *
 * @author Thomas
 */
object LatexDocsFormattingRegexes {

    private val argCommands = listOf(
        LatexRegularCommand.OARG,
        LatexRegularCommand.MARG,
        LatexRegularCommand.PARG
    ).joinToString("|") { it.command }

    private val replacers = listOf(
        // \cs command from the doctools package
        Pair("""(?=[^|]?)\\(cs|cn)\{(?<command>.+?)}""".toRegex(), { result -> "\\" + result.groups["command"]?.value }),
        // Any other commands, hopefully like \textbf, \emph etc
        Pair<Regex, (MatchResult) -> String>("""(?=[^|]?)\\(?!$argCommands)[a-zA-Z_:]+?\{(?<argument>.+?)}""".toRegex(), { result -> result.groups["argument"]?.value ?: "" }),
        // Short verbatim, provided by ltxdoc
        Pair("""\|""".toRegex(), { "" }),
    )

    fun format(docs: String): String {
        var formatted = docs
        replacers.forEach { formatted = it.first.replace(formatted, it.second) }
        return formatted
    }
}