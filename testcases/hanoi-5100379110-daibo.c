#include <stdio.h>

int cd(int d, char a, char b, char c, int sum) {
    if (d == 1) {
        printf("move %c --> %c\n", a, c);
        sum++;
    } else {
        sum = cd(d - 1, a, c, b, sum);
        printf("move %c --> %c\n", a, c);
        sum = cd(d - 1, b, a, c, sum);
        sum++;
    }
    return sum;
}

int main() {
    char a = 'A', b = 'B', c = 'C';
    int d = 10;
    int sum = cd(d, a, b, c, 0);
    printf("%d\n", sum);
    return 0;
}

