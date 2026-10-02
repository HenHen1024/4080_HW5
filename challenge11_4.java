```java
// Chapter 11 Challenge 4
// Store local variables in arrays and access them by index.
//
// Changes are made in:
// Environment.java
// Resolver.java
// Interpreter.java


// ============================================================
// Environment.java
// ============================================================

// Add this field:

private final List<Object> values = new ArrayList<>();


// Add this method:

void defineAt(int index, Object value) {
  while (values.size() <= index) {
    values.add(null);
  }

  values.set(index, value);
}


// Add this method:

Object getAt(int distance, int index) {
  Environment environment = ancestor(distance);
  return environment.values.get(index);
}


// Add this method:

void assignAt(int distance, int index, Object value) {
  ancestor(distance).values.set(index, value);
}


// ============================================================
// Resolver.java
// ============================================================

// Change the scope representation to store indexes:

private final Stack<Map<String, Integer>> scopes = new Stack<>();


// Add this field:

private int nextLocal = 0;


// Change beginScope():

private void beginScope() {
  scopes.push(new HashMap<>());
  nextLocal = 0;
}


// Change declare():

private void declare(Token name) {
  if (scopes.isEmpty()) return;

  Map<String, Integer> scope = scopes.peek();

  if (scope.containsKey(name.lexeme)) {
    Lox.error(name,
        "Already a variable with this name in this scope.");
  }

  scope.put(name.lexeme, nextLocal++);
}


// Change define():

private void define(Token name) {
  if (scopes.isEmpty()) return;
}


// Change resolveLocal():

private void resolveLocal(Expr expr, Token name) {
  for (int i = scopes.size() - 1; i >= 0; i--) {
    Map<String, Integer> scope = scopes.get(i);

    if (scope.containsKey(name.lexeme)) {
      int distance = scopes.size() - 1 - i;
      int index = scope.get(name.lexeme);

      interpreter.resolve(expr, distance, index);
      return;
    }
  }
}


// ============================================================
// Interpreter.java
// ============================================================

// Change locals to store both distance and index:

private final Map<Expr, Integer> locals = new HashMap<>();
private final Map<Expr, Integer> localIndexes = new HashMap<>();


// Change resolve():

void resolve(Expr expr, int depth, int index) {
  locals.put(expr, depth);
  localIndexes.put(expr, index);
}


// Change visitVariableExpr():

@Override
public Object visitVariableExpr(Expr.Variable expr) {
  return lookUpVariable(expr.name, expr);
}


// Change visitAssignExpr():

@Override
public Object visitAssignExpr(Expr.Assign expr) {
  Object value = evaluate(expr.value);

  Integer distance = locals.get(expr);
  if (distance != null) {
    int index = localIndexes.get(expr);
    environment.assignAt(distance, index, value);
  } else {
    globals.assign(expr.name, value);
  }

  return value;
}


// Change lookUpVariable():

private Object lookUpVariable(Token name, Expr expr) {
  Integer distance = locals.get(expr);

  if (distance != null) {
    int index = localIndexes.get(expr);
    return environment.getAt(distance, index);
  }

  return globals.get(name);
}
```
