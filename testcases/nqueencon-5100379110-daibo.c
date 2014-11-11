#include <stdio.h>
#include <stdlib.h>

int printrow(int pos, int c) {
    int i;
    for (i = 1; i <= pos - 1; i++) printf(" .");
    printf(" O");
    for (i = pos + 1; i <= c; i++) printf(" .");
    printf("\n");
    return 0;
}

int nqueen(int n) {
    int i = 0, c = n, odd = n % 2;

    if ((n/2)%3 != 1) {
        printrow(2, c);
        i = 4;
        while (i <= n) {
            printrow(i, c);
            i += 2;
        }
        i = 1;
        while (i <= n) {
            printrow(i, c);
            i += 2;
        }
    } else {
        n = n - odd;
        printrow(n / 2, c);
        i = n / 2 + 1;
        while (i != n / 2 - 1)  {
            printrow(i + 1, c);
            i = (i + 2) % n;
        }

        i = (i - 2)%n;
        while (i !=	n / 2 - 1) {
            printrow(n - i, c);
            i = (i - 2 + n) % n;
        }
        printrow(n - i, c);
        if (odd) printrow(n + 1, c);
    }
    printf("\n");
    return 0;
}

int main() {
    int n;
    for (n = 6; n <= 11; n++)
        nqueen(n);
    return 0;
}

