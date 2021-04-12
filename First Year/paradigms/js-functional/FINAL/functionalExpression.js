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

const wordToOpData = {
    "floor": [floor, 1],
    "ceil": [ceil, 1],
    "_": [floor, 1,],
    "^": [ceil, 1],
    "negate": [negate, 1],
    "+": [add, 2],
    "-": [subtract, 2],
    "*": [multiply, 2],
    "/": [divide, 2],
    "madd": [madd, 3],
    "*+": [madd, 3]
};

const wordToConst = {
    "one": one,
    "two": two
};

function parse(expression) {
    const exps = [];
    const str = expression.trim().split(/\s+/);

    function apply(word) {
        if (word in wordToOpData) {
            const opData = wordToOpData[word];
            return opData[0](...exps.splice(-opData[1]));
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