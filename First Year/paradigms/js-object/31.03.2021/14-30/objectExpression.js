function makeExpression(expression, evaluate, toString, diff) {
    expression.prototype.evaluate = evaluate;
    expression.prototype.toString = toString;
    expression.prototype.diff = diff;
}

function makeOperator(op, opName, dF) {
    let operator = function(...args) {Operator.call(this, ...args);};
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
    x => -x,
    "negate",
    function (varName, x) {
        return new Negate(x.diff(varName))
    });

const Add = makeOperator(
    (a, b) => a + b,
    "+",
    function(varName, a, b) {
        return new Add(
            a.diff(varName),
            b.diff(varName)
        )
    });

const Subtract = makeOperator(
    (a, b) => a - b,
    "-",
    function(varName, a, b) {
        return new Subtract(
            a.diff(varName),
            b.diff(varName)
        )
    });

const Multiply = makeOperator(
    (a, b) => a * b,
    "*",
    function(varName, a, b) {
        return new Add(
            new Multiply(a.diff(varName), b),
            new Multiply(a, b.diff(varName))
        )
    });

const Divide = makeOperator(
    (a, b) => a / b,
    "/",
    function(varName, a, b) {
        return new Divide(
            new Subtract(
                new Multiply(a.diff(varName), b),
                new Multiply(a, b.diff(varName))
            ),
            new Multiply(b, b))
    });



Const.ZERO = new Const(0);
Const.ONE = new Const(1);

const wordToOp = {
    "negate": Negate,
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide
};

const opToArity = {
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate": 1
};

function parse(expression) {
    let stack = [];
    for (const word of expression.trim().split(/\s+/)) {
        if (word in wordToOp) {
            stack.push(new wordToOp[word](...stack.splice(-opToArity[word])));
        } else if (word in wordToVar) {
            stack.push(new Variable(word));
        } else {
            stack.push(new Const(word));
        }
    }
    return stack.pop();
}

// jstest.object.ObjectExpressionTest hard
