#include <stdio.h>

int inline1(int A) {
  printf("%d", A);
  return 1;
}

int inline2(int a, int A) {
  inline1(A);
  a = 1;
  return 1;
}

int main() {
  int a;
  a = 0;
  printf("%d", a);
  inline2(9, a);
  printf("%d", a);
  a = 2;
  inline1(a);
  printf("%d", a);
  return 0;
}