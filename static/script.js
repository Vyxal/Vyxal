(function (mod) {
    if (typeof exports == "object" && typeof module == "object") {
        mod(require("../../lib/codemirror"))
    } else if (typeof define == "function" && define.amd) {
        define(["../../lib/codemirror"], mod)
    } else {
        mod(CodeMirror)
    }
})(function (CodeMirror) {
    "use strict";
    const NUMBER_CHARS = '0123456789.';
    const MOD_CHARS = 'vß⁺₌₍~&';
    const DIGRAPHS = '¨ø∆Þ';
    const CONSTANTS = '₀₁₄₆₇₈¶∞¤ð×';
    const FUNC_CHARS = 'ƛ\'λµ⁽‡≬;';
    const OPENING = '{[(⟨';
    const CLOSING = '}])⟩';
    const VAR_CHARS = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_';
    CodeMirror.defineMode("vyxal", function () {
        return {
            startState: function () {
                return {structure: 'NONE', scc: 0, struct_nest: []}
            },
            token: function (stream, state) {
                if (stream.sol() && state.structure == 'COMMENT') {
                    state.structure = 'NONE'
                }
                var char = stream.next().toString();
                if (state.structure == 'VAR' && !VAR_CHARS.includes(char)) {
                    state.structure = 'NONE'
                }
                if (state.structure == 'VAR' && VAR_CHARS.includes(char)) {
                    return 'var'
                }
                if (state.structure == 'COMMENT') {
                    return 'comment'
                }
                if (char == '#' && state.structure == 'NONE') {
                    state.structure = 'COMMENT';
                    return 'comment'
                }
                if (state.structure == 'LAMBDA_ARITY') {
                    if (char == '|') {
                        state.structure = 'NONE'
                    }
                    return 'function'
                }
                if (state.structure == 'FUNC_DEF') {
                    if (char == '|') {
                        state.structure = 'NONE'
                    }
                    return 'function'
                }
                if (state.structure == 'FUNC_REF') {
                    if (char == ';') {
                        state.structure = 'NONE'
                    }
                    return 'function'
                }
                if (state.structure == 'FUNC_CALL') {
                    if (char == ';') {
                        structure = 'NONE'
                    }
                    return 'function'
                }
                if (state.structure == 'STRING' && char !== '`') {
                    return 'string'
                }
                if (state.structure == 'CHAR') {
                    state.structure = 'NONE';
                    return 'string'
                }
                if ((state.structure == 'COMP_STRING' && char !== '«') || (state.structure == 'COMP_INT' && char !== '»')) {
                    return 'comp'
                }
                if (char == '`' && (state.structure == 'NONE' || state.structure == 'STRING')) {
                    if (state.structure == 'STRING') {
                        state.structure = 'NONE'
                    } else {
                        state.structure = 'STRING'
                    }
                    return 'string'
                }
                if (state.structure == 'DIGRAPH') {
                    state.structure = 'NONE';
                    return 'digraph'
                }
                if (char == '\\' && state.structure == 'NONE') {
                    state.structure = 'CHAR';
                    return 'string'
                }
                if (char == '‛' && state.structure == 'NONE') {
                    state.structure = 'SCC';
                    state.scc = 2;
                    return 'string'
                }
                
                
                if (char == '«') {
                    if (state.structure == 'COMP_STRING') {
                        state.structure = 'NONE'
                    } else {
                        state.structure = 'COMP_STRING'
                    }
                    return 'comp'
                }
                if (char == '»') {
                    if (state.structure == 'COMP_INT') {
                        state.structure = 'NONE'
                    } else {
                        state.structure = 'COMP_INT'
                    }
                    return 'comp'
                }
                if (NUMBER_CHARS.includes(char) && state.structure == 'NONE') {
                    return 'number'
                }
                if (MOD_CHARS.includes(char) && state.structure == 'NONE') {
                    return 'mod'
                }
                if (FUNC_CHARS.includes(char) && state.structure == 'NONE') {
                    if (char == 'λ' && stream.match(/^\d+\|/, false)) {
                        state.structure = 'LAMBDA_ARITY'
                    }
                    return 'function'
                }
                if (OPENING.includes(char)) {
                    state.struct_nest.push(char);
                    if (char == '(') {
                        if (stream.match(/^[a-zA-Z_]+\|/, false)) {
                            state.structure = 'VAR'
                        }
                    }
                    if (char == '⟨') {
                        return 'list'
                    }
                    return 'keyword'
                }
                if ('←→'.includes(char) && state.structure == 'NONE') {
                    state.structure = 'VAR';
                    return 'var'
                }
                if (char == '@' && state.structure == 'NONE') {
                    if (stream.match(/^[a-zA-Z_]+(\:([a-zA-Z_]|\d)+)*|/)) {
                        state.structure = 'FUNC_DEF'
                    } else {
                        state.structure = 'FUNC_CALL'
                    }
                    return 'function'
                }
                if (char == '|' && (state.structure == 'NONE' || state.structure == 'VAR')) {
                    if ([...state.struct_nest].pop() == '⟨') {
                        return 'list'
                    }
                    state.structure = 'NONE';
                    return 'keyword'
                }
                if (char == '°' && state.structure == 'NONE') {
                    state.structure = 'FUNC_REF';
                    return 'function'
                }
                if (CLOSING.includes(char)) {
                    state.struct_nest.pop();
                    if (char == '⟩') {
                        return 'list'
                    }
                    return 'keyword'
                }
                if (CONSTANTS.includes(char) || state.structure == 'CONSTANT') {
                    if (state.structure == 'CONSTANT') {
                        state.structure = 'NONE'
                    }
                    return 'constant'
                }
                if (DIGRAPHS.includes(char)) {
                    state.structure = 'DIGRAPH';
                    return 'digraph'
                }
                if (char == 'k') {
                    state.structure = 'CONSTANT';
                    return 'constant'
                }
                return 'none'
            }
        }
    })
});