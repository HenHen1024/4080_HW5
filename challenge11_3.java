```java
// Chapter 11 Challenge 3
// Report an error if a local variable is never used.
//
// Changes are made in:
// Resolver.java


// ============================================================
// Resolver.java
// ============================================================

// Change the scope representation.
//
// Instead of:
// Map<String, Boolean>
//
// Use:

private final Stack<Map<String, Boolean>> scopes = new Stack<>();


// Change declare():

private void declare(Token name) {
  if (scopes.isEmpty()) return;

  Map<String, Boolean> scope = scopes.peek();

  if (scope.containsKey(name.lexeme)) {
    Lox.error(name,
        "Already a variable with this name in this scope.");
  }

  scope.put(name.lexeme, false);
}


// Change define():

private void define(Token name) {
  if (scopes.isEmpty()) return;

  scopes.peek().put(name.lexeme, false);
}


// Change resolveLocal():

private void resolveLocal(Expr expr, Token name) {
  for (int i = scopes.size() - 1; i >= 0; i--) {
    if (scopes.get(i).containsKey(name.lexeme)) {
      scopes.get(i).put(name.lexeme, true);

      int distance = scopes.size() - 1 - i;
      interpreter.resolve(expr, distance);
      return;
    }
  }
}


// When ending a scope, check for unused variables.
//
// Change endScope() to:

private void endScope() {
  Map<String, Boolean> scope = scopes.pop();

  for (Map.Entry<String, Boolean> entry : scope.entrySet()) {
    if (!entry.getValue()) {
      Lox.error(new Token(
          TokenType.IDENTIFIER,
          entry.getKey(),
          null,
          0),
          "Local variable is never used.");
    }
  }
}
```
