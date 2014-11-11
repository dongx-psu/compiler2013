#include <stdio.h>

int hanoi(char a, char b, char c, int n) {
  if (n > 1) {
    hanoi(a, c, b, n-1);
    printf("%c -> %c\n", a, c);
    hanoi(b, a, c, n-1);
  }
  else {
    printf("%c -> %c\n", a, c);
  }
}

int main() {
  int N;
  N = 12;
  if (N > 0) hanoi('a', 'b', 'c', N);
  return 0;
}
