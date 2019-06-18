package jg.rhex.compile.components;

/*
 * GramPracParser.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 */

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A token stream parser.
 *
 *
 */
public class GramPracParser extends RecursiveDescentParser {

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_1 = 3001;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_2 = 3002;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_3 = 3003;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_4 = 3004;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_5 = 3005;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_6 = 3006;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_7 = 3007;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_8 = 3008;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_9 = 3009;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_10 = 3010;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_11 = 3011;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_12 = 3012;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_13 = 3013;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_14 = 3014;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_15 = 3015;

    /**
     * Creates a new parser with a default analyzer.
     *
     * @param in             the input stream to read from
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public GramPracParser(Reader in) throws ParserCreationException {
        super(in);
        createPatterns();
    }

    /**
     * Creates a new parser.
     *
     * @param in             the input stream to read from
     * @param analyzer       the analyzer to use while parsing
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public GramPracParser(Reader in, GramPracAnalyzer analyzer)
        throws ParserCreationException {

        super(in, analyzer);
        createPatterns();
    }

    /**
     * Creates a new tokenizer for this parser. Can be overridden by a
     * subclass to provide a custom implementation.
     *
     * @param in             the input stream to read from
     *
     * @return the tokenizer created
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    protected Tokenizer newTokenizer(Reader in)
        throws ParserCreationException {

        return new GramPracTokenizer(in);
    }

    /**
     * Initializes the parser by creating all the production patterns.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;

        pattern = new ProductionPattern(GramPracConstants.ASSGN,
                                        "assgn");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.EXPR, 1, 1);
        alt.addProduction(SUBPRODUCTION_2, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.EXPR,
                                        "expr");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.COMPARISON, 1, 1);
        alt.addProduction(SUBPRODUCTION_4, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.COMPARISON,
                                        "comparison");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.ADDITION, 1, 1);
        alt.addProduction(SUBPRODUCTION_6, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.ADDITION,
                                        "addition");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.MULTIPLICATION, 1, 1);
        alt.addProduction(SUBPRODUCTION_8, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.MULTIPLICATION,
                                        "multiplication");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.UNARY, 1, 1);
        alt.addProduction(SUBPRODUCTION_10, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.UNARY,
                                        "unary");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.BANG, 0, 1);
        alt.addProduction(GramPracConstants.UNIT, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.FUNC_ARG,
                                        "func_arg");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.EXPR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.FUNC_ARGS,
                                        "func_args");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.FUNC_ARG, 1, 1);
        alt.addProduction(SUBPRODUCTION_11, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.FUNC_CALL,
                                        "func_call");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NAME, 1, 1);
        alt.addToken(GramPracConstants.OP_PAREN, 1, 1);
        alt.addProduction(GramPracConstants.FUNC_ARGS, 0, 1);
        alt.addToken(GramPracConstants.CL_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.PAREN_EXPR,
                                        "paren_expr");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.OP_PAREN, 1, 1);
        alt.addProduction(GramPracConstants.EXPR, 1, 1);
        alt.addToken(GramPracConstants.CL_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.INVOKE,
                                        "invoke");
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_13, 1, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.GENERIC,
                                        "generic");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.LESS, 1, 1);
        alt.addProduction(GramPracConstants.TYPE_NAME, 1, 1);
        alt.addProduction(SUBPRODUCTION_14, 0, -1);
        alt.addToken(GramPracConstants.GREAT, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.TYPE_NAME,
                                        "type_name");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NAME, 1, 1);
        alt.addProduction(GramPracConstants.GENERIC, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.CAST,
                                        "cast");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.AS, 1, 1);
        alt.addProduction(GramPracConstants.TYPE_NAME, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.ARRAY_ACC,
                                        "array_acc");
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.OP_SQ_BRACK, 1, 1);
        alt.addProduction(GramPracConstants.EXPR, 1, 1);
        alt.addToken(GramPracConstants.CL_SQ_BRACK, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GramPracConstants.UNIT,
                                        "unit");
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_15, 1, 1);
        alt.addProduction(GramPracConstants.CAST, 0, -1);
        alt.addProduction(GramPracConstants.INVOKE, 0, 1);
        alt.addProduction(GramPracConstants.ARRAY_ACC, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_1,
                                        "Subproduction1");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQUAL, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_MULT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_ADD, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_DIV, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_MIN, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_MOD, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_2,
                                        "Subproduction2");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_1, 1, 1);
        alt.addProduction(GramPracConstants.EXPR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_3,
                                        "Subproduction3");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NOT_EQ, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_EQ, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_4,
                                        "Subproduction4");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_3, 1, 1);
        alt.addProduction(GramPracConstants.COMPARISON, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_5,
                                        "Subproduction5");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.GREAT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.GR_EQ, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.LESS, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.LS_EQ, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.EQ_EQ, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_6,
                                        "Subproduction6");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_5, 1, 1);
        alt.addProduction(GramPracConstants.ADDITION, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_7,
                                        "Subproduction7");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.MINUS, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.PLUS, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_8,
                                        "Subproduction8");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_7, 1, 1);
        alt.addProduction(GramPracConstants.MULTIPLICATION, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_9,
                                        "Subproduction9");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.DIV, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.MULT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.MOD, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_10,
                                        "Subproduction10");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addProduction(SUBPRODUCTION_9, 1, 1);
        alt.addProduction(GramPracConstants.UNARY, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_11,
                                        "Subproduction11");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.COMMA, 1, 1);
        alt.addProduction(GramPracConstants.FUNC_ARG, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_12,
                                        "Subproduction12");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NAME, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.FUNC_CALL, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_13,
                                        "Subproduction13");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.DOT, 1, 1);
        alt.addProduction(SUBPRODUCTION_12, 1, 1);
        alt.addProduction(GramPracConstants.ARRAY_ACC, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_14,
                                        "Subproduction14");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.COMMA, 1, 1);
        alt.addProduction(GramPracConstants.TYPE_NAME, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_15,
                                        "Subproduction15");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.INTEGER, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.DOUBLE, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.FLOAT, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.LONG, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.CHAR, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(GramPracConstants.PAREN_EXPR, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NEW, 0, 1);
        alt.addProduction(GramPracConstants.FUNC_CALL, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GramPracConstants.NAME, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
