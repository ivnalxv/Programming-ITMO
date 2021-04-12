function createExpression(expression, evaluate, toString, diff) {
    expression.prototype.evaluate = evaluate;
    expression.prototype.toString = toString;
    expression.prototype.diff = diff;
}

function createOperator(op, opName, diffRule) {
    let operator = function(...args) {Operator.call(this, ...args);};
    operator.prototype = Object.create(Operator.prototype);
    operator.prototype._operator = op;
    operator.prototype._opName = opName;
    operator.prototype._diffRule = diffRule;
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

function Operator(...args) {
    this._args = args;
}

createExpression(
    Const,
    function() { return +this._value},
    function() { return "" + this._value},
    function() { return Const.ZERO}
);

createExpression(
    Variable,
    function(...vars) { return vars[wordToVar[this._varName]]},
    function() { return this._varName},
    function(name) { return this._varName === name ? Const.ONE : Const.ZERO});


createExpression(
    Operator,
    function(...vars) { return this._operator(...this._args.map(val => val.evaluate(...vars)))},
    function() { return this._args.join(" ") + " " + this._opName},
    function (name) { return this._diffRule(name, ...this._args)});


let Negate = createOperator(
    x => -x,
    "negate",
    function (varName, x) {return new Negate(x.diff(varName))
    });

const Add = createOperator(
    (a, b) => a + b,
    "+",
    function(varName, a, b) { return new Add(a.diff(varName), b.diff(varName))});

const Subtract = createOperator(
    (a, b) => a - b,
    "-",
    function(varName, a, b) { return new Subtract(a.diff(varName), b.diff(varName))});

const Multiply = createOperator(
    (a, b) => a * b,
    "*",
    function(varName, a, b) {
        return new Add(
            new Multiply(a.diff(varName), b),
            new Multiply(a, b.diff(varName)))});

const Divide = createOperator(
    (a, b) => a / b,
    "/",
    function(varName, a, b) {
        return new Divide(
            new Subtract(
                new Multiply(a.diff(varName), b),
                new Multiply(a, b.diff(varName))),
            new Multiply(b, b))});



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
