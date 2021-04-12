"use strict";

function makeExpression(expression, evaluate, toString, diff) {
    expression.prototype.evaluate = evaluate;
    expression.prototype.toString = toString;
    expression.prototype.diff = diff;
}

function makeOperator(opName, op, dF) {
    const operator = function(...innerExprs) {Operator.call(this, ...innerExprs);};
    operator.prototype = Object.create(Operator.prototype);
    operator.prototype._operator = op;
    operator.prototype._opName = opName;
    operator.prototype._dF = dF;
    return operator;
}

const wordToVar = {
    "x": 0,
    "y": 1,
    "z": 2
}

function Const(value) {
    this._value = value;
}

function Variable(varName) {
    this._varName = varName;
}

function Operator(...innerExprs) {
    this._innerExprs = innerExprs;
}

makeExpression(
    Const,
    function() { return +this._value},
    function() { return "" + this._value},
    function() { return Const.ZERO}
);

makeExpression(
    Variable,
    function(...vars) { return vars[wordToVar[this._varName]]},
    function() { return this._varName},
    function(varName) { return this._varName === varName ? Const.ONE : Const.ZERO});


makeExpression(
    Operator,
    function(...vars) { return this._operator(...this._innerExprs.map(value => value.evaluate(...vars)))},
    function() { return this._innerExprs.join(" ") + " " + this._opName},
    function (varName) { return this._dF(varName, ...this._innerExprs)});


const Negate = makeOperator(
    "negate",
    a => -a,
    function (varName, a) {
        return new Negate(a.diff(varName))
    });

const Add = makeOperator(
    "+",
    (a, b) => a + b,
    function(varName, a, b) {
        return new Add(
            a.diff(varName),
            b.diff(varName)
        )
    });

const Subtract = makeOperator(
    "-",
    (a, b) => a - b,
    function(varName, a, b) {
        return new Subtract(
            a.diff(varName),
            b.diff(varName)
        )
    });

const Multiply = makeOperator(
    "*",
    (a, b) => a * b,
    function(varName, a, b) {
        return new Add(
            new Multiply(a.diff(varName), b),
            new Multiply(a, b.diff(varName))
        )
    });

const Divide = makeOperator(
    "/",
    (a, b) => a / b,
    function(varName, a, b) {
        return new Divide(
            new Subtract(
                new Multiply(a.diff(varName), b),
                new Multiply(a, b.diff(varName))
            ),
            new Multiply(b, b))
    });

const Hypot = makeOperator(
    "hypot",
    (a, b) => a*a + b*b,
    function(varName, a, b) {
        return new Add(
            new Multiply(a, a).diff(varName),
            new Multiply(b, b).diff(varName)
        )
    });

const HMean = makeOperator(
    "hmean",
    (a, b) => 2/(1/a + 1/b),
    function(varName, a, b) {
        return new Multiply(
            Const.TWO,
            new Divide(new Multiply(a, b),
                new Add(a, b)
            ).diff(varName)
        )
    });


Const.ZERO = new Const(0);
Const.ONE = new Const(1);
Const.TWO = new Const(2);

const wordToOpData = {
    "hypot": [Hypot, 2],
    "hmean": [HMean, 2],
    "negate": [Negate, 1],
    "+": [Add, 2],
    "-": [Subtract, 2],
    "*": [Multiply, 2],
    "/": [Divide, 2]
};

function parse(expression) {
    const exps = [];
    const str = expression.trim().split(/\s+/);

    function apply(word) {
        if (word in wordToOpData) {
            const opData = wordToOpData[word];
            return new opData[0](...exps.splice(-opData[1]));
        } else if (word in wordToVar) {
            return new Variable(word);
        }
        return new Const(word);
    }

    for (const word of str) {
        exps.push(apply(word));
    }

    return exps.pop();
}

// jstest.object.ObjectExpressionTest hard
