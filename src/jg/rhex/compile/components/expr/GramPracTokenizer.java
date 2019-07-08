/*
 * GramPracTokenizer.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 */

package jg.rhex.compile.components.expr;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 *
 *
 */
public class GramPracTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public GramPracTokenizer(Reader input)
        throws ParserCreationException {

        super(input, false);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(GramPracConstants.WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.REGEXP_TYPE,
                                   "[ \\t\\n\\r]+");
        pattern.setIgnore();
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.STRING,
                                   "STRING",
                                   TokenPattern.REGEXP_TYPE,
                                   "\\\".*?\\\"");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CHAR,
                                   "CHAR",
                                   TokenPattern.REGEXP_TYPE,
                                   "'[a-zA-Z]'|'[0-9]'");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.INTEGER,
                                   "INTEGER",
                                   TokenPattern.REGEXP_TYPE,
                                   "([0-9]+)");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.LONG,
                                   "LONG",
                                   TokenPattern.REGEXP_TYPE,
                                   "([0-9]+L|[0-9]+l)");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FLOAT,
                                   "FLOAT",
                                   TokenPattern.REGEXP_TYPE,
                                   "(((\\d+\\.\\d+|\\d+\\.\\d+)(f|F))|([0-9]+(f|F)))");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.DOUBLE,
                                   "DOUBLE",
                                   TokenPattern.REGEXP_TYPE,
                                   "(\\d+\\.\\d+|\\d+\\.\\d+)");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.TRUE,
                                   "TRUE",
                                   TokenPattern.STRING_TYPE,
                                   "true");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FALSE,
                                   "FALSE",
                                   TokenPattern.STRING_TYPE,
                                   "false");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.NULL,
                                   "NULL",
                                   TokenPattern.STRING_TYPE,
                                   "null");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.THIS,
                                   "THIS",
                                   TokenPattern.STRING_TYPE,
                                   "this");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.VOID,
                                   "VOID",
                                   TokenPattern.STRING_TYPE,
                                   "void");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.TYPE,
                                   "TYPE",
                                   TokenPattern.STRING_TYPE,
                                   "type");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.INFER,
                                   "INFER",
                                   TokenPattern.STRING_TYPE,
                                   "infer");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.NEW,
                                   "NEW",
                                   TokenPattern.STRING_TYPE,
                                   "new");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.AS,
                                   "AS",
                                   TokenPattern.STRING_TYPE,
                                   "as");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.PRIV,
                                   "PRIV",
                                   TokenPattern.STRING_TYPE,
                                   "private");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.PUBL,
                                   "PUBL",
                                   TokenPattern.STRING_TYPE,
                                   "public");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.PRO,
                                   "PRO",
                                   TokenPattern.STRING_TYPE,
                                   "protected");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.ABSTRACT,
                                   "ABSTRACT",
                                   TokenPattern.STRING_TYPE,
                                   "abstract");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FINAL,
                                   "FINAL",
                                   TokenPattern.STRING_TYPE,
                                   "final");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.SYNCH,
                                   "SYNCH",
                                   TokenPattern.STRING_TYPE,
                                   "synchronized");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.THROWS,
                                   "THROWS",
                                   TokenPattern.STRING_TYPE,
                                   "throws");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.STATIC,
                                   "STATIC",
                                   TokenPattern.STRING_TYPE,
                                   "static");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.VOLATILE,
                                   "VOLATILE",
                                   TokenPattern.STRING_TYPE,
                                   "volatile");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FUNC,
                                   "FUNC",
                                   TokenPattern.STRING_TYPE,
                                   "func");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.RETURN,
                                   "RETURN",
                                   TokenPattern.STRING_TYPE,
                                   "return");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FOR,
                                   "FOR",
                                   TokenPattern.STRING_TYPE,
                                   "for");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.WHILE,
                                   "WHILE",
                                   TokenPattern.STRING_TYPE,
                                   "while");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.IF,
                                   "IF",
                                   TokenPattern.STRING_TYPE,
                                   "if");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.ELSE,
                                   "ELSE",
                                   TokenPattern.STRING_TYPE,
                                   "else");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.SWITCH,
                                   "SWITCH",
                                   TokenPattern.STRING_TYPE,
                                   "switch");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.DO,
                                   "DO",
                                   TokenPattern.STRING_TYPE,
                                   "do");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.DEF,
                                   "DEF",
                                   TokenPattern.STRING_TYPE,
                                   "default");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CASE,
                                   "CASE",
                                   TokenPattern.STRING_TYPE,
                                   "case");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CONT,
                                   "CONT",
                                   TokenPattern.STRING_TYPE,
                                   "continue");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.BREAK,
                                   "BREAK",
                                   TokenPattern.STRING_TYPE,
                                   "break");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CLASS,
                                   "CLASS",
                                   TokenPattern.STRING_TYPE,
                                   "class");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.INTER,
                                   "INTER",
                                   TokenPattern.STRING_TYPE,
                                   "interface");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EXTNDS,
                                   "EXTNDS",
                                   TokenPattern.STRING_TYPE,
                                   "extends");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.TPARAM,
                                   "TPARAM",
                                   TokenPattern.STRING_TYPE,
                                   "tparam");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.USE,
                                   "USE",
                                   TokenPattern.STRING_TYPE,
                                   "use");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.FROM,
                                   "FROM",
                                   TokenPattern.STRING_TYPE,
                                   "from");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.THROW,
                                   "THROW",
                                   TokenPattern.STRING_TYPE,
                                   "throw");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.NAME,
                                   "NAME",
                                   TokenPattern.REGEXP_TYPE,
                                   "[a-zA-Z][a-zA-Z0-9_]*");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.ARROW,
                                   "ARROW",
                                   TokenPattern.STRING_TYPE,
                                   "->");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.PLUS,
                                   "PLUS",
                                   TokenPattern.STRING_TYPE,
                                   "+");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.MINUS,
                                   "MINUS",
                                   TokenPattern.STRING_TYPE,
                                   "-");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.MULT,
                                   "MULT",
                                   TokenPattern.STRING_TYPE,
                                   "*");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.DIV,
                                   "DIV",
                                   TokenPattern.STRING_TYPE,
                                   "/");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.MOD,
                                   "MOD",
                                   TokenPattern.STRING_TYPE,
                                   "%");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.AND,
                                   "AND",
                                   TokenPattern.STRING_TYPE,
                                   "&");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.OR,
                                   "OR",
                                   TokenPattern.STRING_TYPE,
                                   "|");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.BANG,
                                   "BANG",
                                   TokenPattern.STRING_TYPE,
                                   "!");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.WLDCRD,
                                   "WLDCRD",
                                   TokenPattern.STRING_TYPE,
                                   "?");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.COLON,
                                   "COLON",
                                   TokenPattern.STRING_TYPE,
                                   ":");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.SEMICOLON,
                                   "SEMICOLON",
                                   TokenPattern.STRING_TYPE,
                                   ";");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EXPONENT,
                                   "EXPONENT",
                                   TokenPattern.STRING_TYPE,
                                   "^");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.LESS,
                                   "LESS",
                                   TokenPattern.STRING_TYPE,
                                   "<");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.GREAT,
                                   "GREAT",
                                   TokenPattern.STRING_TYPE,
                                   ">");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQUAL,
                                   "EQUAL",
                                   TokenPattern.STRING_TYPE,
                                   "=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_EQ,
                                   "EQ_EQ",
                                   TokenPattern.STRING_TYPE,
                                   "==");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.NOT_EQ,
                                   "NOT_EQ",
                                   TokenPattern.STRING_TYPE,
                                   "!=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.GR_EQ,
                                   "GR_EQ",
                                   TokenPattern.STRING_TYPE,
                                   ">=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.LS_EQ,
                                   "LS_EQ",
                                   TokenPattern.STRING_TYPE,
                                   "<=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_MULT,
                                   "EQ_MULT",
                                   TokenPattern.STRING_TYPE,
                                   "*=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_ADD,
                                   "EQ_ADD",
                                   TokenPattern.STRING_TYPE,
                                   "+=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_DIV,
                                   "EQ_DIV",
                                   TokenPattern.STRING_TYPE,
                                   "/=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_MIN,
                                   "EQ_MIN",
                                   TokenPattern.STRING_TYPE,
                                   "-=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.EQ_MOD,
                                   "EQ_MOD",
                                   TokenPattern.STRING_TYPE,
                                   "%=");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.BOOL_AND,
                                   "BOOL_AND",
                                   TokenPattern.STRING_TYPE,
                                   "&&");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.BOOL_OR,
                                   "BOOL_OR",
                                   TokenPattern.STRING_TYPE,
                                   "||");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.OP_PAREN,
                                   "OP_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CL_PAREN,
                                   "CL_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.COMMA,
                                   "COMMA",
                                   TokenPattern.STRING_TYPE,
                                   ",");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.DOT,
                                   "DOT",
                                   TokenPattern.STRING_TYPE,
                                   ".");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.OP_SQ_BRACK,
                                   "OP_SQ_BRACK",
                                   TokenPattern.STRING_TYPE,
                                   "[");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CL_SQ_BRACK,
                                   "CL_SQ_BRACK",
                                   TokenPattern.STRING_TYPE,
                                   "]");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.OP_CU_BRACK,
                                   "OP_CU_BRACK",
                                   TokenPattern.STRING_TYPE,
                                   "{");
        addPattern(pattern);

        pattern = new TokenPattern(GramPracConstants.CL_CU_BRACK,
                                   "CL_CU_BRACK",
                                   TokenPattern.STRING_TYPE,
                                   "}");
        addPattern(pattern);
    }
}
