/**
 * calculate gcd, nothing special
 * by msh
 */
#include <stdio.h>

int gcd(int x, int y) {
  if (x%y == 0) return y;
  else return gcd(y, x%y);
}

int main() {
  printf("%d\n%d\n%d\n%d\n",
    gcd(10,1),
    gcd(50,35),
    gcd(34986,3087),
    gcd(2907,1539));
  return 0;
}
