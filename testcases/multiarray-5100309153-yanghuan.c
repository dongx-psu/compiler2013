#include <stdio.h>

typedef struct rec {
    int num;
    int c;
} rec;

int printNum(int num) {
    printf("%d", num);
    printf("\n");
}
int main() {
    int** a;
    rec* b;
    int i, j;
    a = malloc(4 * sizeof(int*));
    for (i = 0; i < 10; i = i + 1)
        a[i] = malloc(10 * sizeof(int));

    for (i = 0; i < 4; i = i + 1) {
        for (j = 0; j < 10; j = j + 1)
            a[i][j] = 888;
    }
    b = malloc(5 * sizeof(rec));
    for (i = 0; i < 5; i = i + 1) {
        b[i].num = -1;
    }
    
    printNum(a[3][9]);
    for (i = 0; i <= 3; i = i + 1)
        for (j = 0; j <= 9; j = j + 1)
            a[i][j] = i * 10 + j; 
    
    for (i = 0; i <= 3; i = i + 1)
        for (j = 0; j <= 9; j = j + 1)
            printNum(a[i][j]);
    a[2][10]=0;
    printNum(a[2][10]);
    printf("\n");
    b[0].num = -2;
    b[a[2][10]].num = -10;
    printNum(b[0].num);
    printNum(b[1].num);
    return 0;
}
