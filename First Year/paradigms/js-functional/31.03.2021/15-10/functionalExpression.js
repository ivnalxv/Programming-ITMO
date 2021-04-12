"use strict";

const operator = op => (...args) => (...vars) => op(...args.map(value => value(...vars)));

const madd = operator((a, b, c) => a * b + c);

const subtract = operator((a, b) => a - b);
const multiply = operator((a, b) => a * b);
const divide = operator((a, b) => a / b);
const add = operator((a, b) => a + b);

const floor = operator(a => Math.floor(a));
const ceil = operator(a => Math.ceil(a));
const negate = operator(a => -a);

const wordToVar = {
    "x": 0,
    "y": 1,
    "z": 2
};

const cnst = value => () => +value;
const variable = word => (...vars) => vars[wordToVar[word]];

const one = cnst(1);
const two = cnst(2);

const wordToOp = {
    "madd": madd,
    "*+": madd,
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "negate": negate,
    "floor": floor,
    "ceil": ceil,
    "_": floor,
    "^": ceil
};

const opToArity = {
    "madd": 3,
    "*+": 3,
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate": 1,
    "floor": 1,
    "ceil": 1,
    "_": 1,
    "^": 1
};

const wordToConst = {
    "one": one,
    "two": two
};

function parse(expression) {
    const exps = [];
    const str = expression.trim().split(/\s+/);

    function apply(word) {
        if (word in wordToOp) {
            return wordToOp[word](...exps.splice(-opToArity[word]));
        } else if (word in wordToConst) {
            return wordToConst[word];
        } else if (word in wordToVar) {
            return variable(word);
        }
        return cnst(word);
    }

    for (const word of str) {
        exps.push(apply(word));
    }

    return exps.pop();
}

// jstest.functional.FunctionalOneFPTest hard