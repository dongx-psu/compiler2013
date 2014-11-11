#include <stdio.h>

int plus(int SIZE, int* a, int* b, int* c){
    int add, j;
    add = 0;
    j = 0;
    while (j < SIZE){
        c[j] = a[j] + b[j] + add;     
        add = 0;
        if (c[j] > 9)  {
            c[j] = c[j] - 10;
            add = 1;
        }
        j=j+1;
    }
    if (add > 0) {
        c[j] = 1;
        return j;
    }
    else return j - 1;
}

int printIntA(int L, int* a) {  
    while (L >= 0) {
        printf("%d", a[L]);
        L=L-1;
    }
    printf("\n");
}

int printIntB(int L, int* b) {
    while (L >= 0) {
        printf("%d", b[L]);
        L=L-1;
    }
    printf("\n");
}
  
int printBigInt(int L, int* c) {
    while (L >= 0) {
        printf("%d", c[L]);
        L=L-1;
    }
    printf("\n");
}

int main() {
    int SIZE;
    int* a;
    int* b;
    int* c;
    int L, i;
  
    SIZE = 15;
    a = malloc(SIZE * sizeof(int));
    for (i = 0; i < SIZE; i = i + 1)
        a[i] = 0;
    b = malloc(SIZE * sizeof(int));
    for (i = 0; i < SIZE; i = i + 1)
        b[i] = 0;
    c = malloc(2 * SIZE * sizeof(int));
    for (i = 0; i < 2 * SIZE; i = i + 1)
        c[i] = 0;

    L = 0;
    
    for (i = 0; i < SIZE; i = i + 1) {
        if (i < 9) a[i]=i+1;
        else a[i] = i-9;
    }
    printIntA(SIZE-1, a);
    for (i = 0; i < SIZE; i = i + 1) {
        if (i < SIZE / 2) b[i] = 7;
        else b[i] = 3;
    }
    printIntB(SIZE-1, b);
    L = plus(SIZE, a, b, c);
    printBigInt(L, c);
    return 0;
}