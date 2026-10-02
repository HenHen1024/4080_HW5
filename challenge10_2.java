```java
// Chapter 10 Challenge 2
// Add anonymous functions (lambdas) to Lox.
//
// Changes are made in:
// GenerateAst.java
// LoxFunction.java
// Parser.java
// Interpreter.java


// ============================================================
// GenerateAst.java
// ============================================================

// Add this to the Expr types:

"Function : List<Token> parameters, List<Stmt> body",


// Change the Function Stmt type from:
//
// "Function   : Token name, List<Token> params,  List<Stmt> body",
//
// to:

"Function   : Token name, Expr.Function function",


// ============================================================
// LoxFunction.java
// ============================================================

// Change the fields to:

private final String name;
private final Expr.Function declaration;
private final Environment closure;


// Change the constructor to:

LoxFunction(String name, Expr.Function declaration, Environment closure) {
  this.name = name;
  this.closure = closure;
  this.declaration = declaration;
}


// Change toString():

@Override
public String toString() {
  if (name == null) return "<fn>";
  return "<fn " + name + ">";
}


// The call() method should use:
//
// declaration.parameters
// declaration.body
//
// instead of the old Stmt.Function fields.


// ============================================================
// Parser.java
// ============================================================

// Change function() to:

private Stmt.Function function(String kind) {
  Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
  return new Stmt.Function(name, functionBody(kind));
}


// Add functionBody():

private Expr.Function functionBody(String kind) {
  consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");

  List<Token> parameters = new ArrayList<>();
  if (!check(RIGHT_PAREN)) {
    do {
      if (parameters.size() >= 8) {
        error(peek(), "Can't have more than 8 parameters.");
      }

      parameters.add(
          consume(IDENTIFIER, "Expect parameter name."));
    } while (match(COMMA));
  }

  consume(RIGHT_PAREN, "Expect ')' after parameters.");

  consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
  List<Stmt> body = block();

  return new Expr.Function(parameters, body);
}


// Add this to primary():

if (match(FUN)) return functionBody("function");


// Add this method for one-token lookahead:

private boolean checkNext(TokenType tokenType) {
  if (isAtEnd()) return false;
  if (tokens.get(current + 1).type == EOF) return false;
  return tokens.get(current + 1).type == tokenType;
}


// In declaration(), change the function declaration check.
//
// Instead of:
//
// if (match(FUN)) return function("function");
//
// use:

if (check(FUN) && checkNext(IDENTIFIER)) {
  consume(FUN, null);
  return function("function");
}


// ============================================================
// Interpreter.java
// ============================================================

// Change visitFunctionStmt():

@Override
public Void visitFunctionStmt(Stmt.Function stmt) {
  String fnName = stmt.name.lexeme;
  environment.define(
      fnName,
      new LoxFunction(fnName, stmt.function, environment));
  return null;
}


// Add visitFunctionExpr():

@Override
public Object visitFunctionExpr(Expr.Function expr) {
  return new LoxFunction(null, expr, environment);
}
```
