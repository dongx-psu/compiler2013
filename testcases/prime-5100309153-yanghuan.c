#include <stdio.h>

int getPrime(int N, int* primeCount, int* bool, int* gps, int* prime) {
    int count, i;
    count = 2;
    for (i = 2; i <= N; i = i + 1) {
        if (bool[i] == 1) {
            primeCount[0] = primeCount[0] + 1;
            prime[primeCount[0]] = i;
            gps[i] = primeCount[0];
        }
        while (i * count <= N) {
            bool[i * count] = 0;
            count = count + 1;
        }
        count = 2;
    }
}

int getResult(int N, int k1, int k2, int* bool, int* gps, int* prime, int** result) {
    if (result[k1][k2] == -1) 
        if (prime[k2] * 2 - prime[k1] <= N) 
            if (bool[prime[k2] * 2 - prime[k1]]) 
                result[k1][k2] = getResult(N,
                                k2, 
                                gps[prime[k2] * 2 - prime[k1]],
                                bool,
                                gps,
                                prime,
                                result) + 1;
    if (result[k1][k2] == -1) 
        result[k1][k2] = 1;
    return result[k1][k2];
}

int printF(int k1, int k2, int k3) {
    printf("%d", k1);
    while (k3 > 0) {
        printf("%c", ' ');
        printf("%d", k2);
        k2 = k2 * 2 - k1;
        k1 = (k1 + k2) / 2;
        k3 = k3 - 1;
    }
    printf("%c", '\n');
}

int main() {
    int N, M, i, j, primeCount, resultCount;
    int *bool, *prime, *gps, *tmp;
    int **result;
    N = 1000;
    M = 168;
    primeCount = 0;
    resultCount = 0;
    tmp = malloc(1 * sizeof(int));
    tmp[0] = 0;
    bool = malloc((N + 1) * sizeof(int));
    prime = malloc((M + 1) * sizeof(int));
    gps = malloc((N + 1) * sizeof(int));
    for (i = 0; i < N + 1; i = i + 1)
    {
        bool[i] = 1;
        gps[i] = 0;
    }
    for (i = 0; i < M + 1; i = i + 1)
    {
        prime[i] = 0;
    }
    result = malloc((M + 1) * sizeof(int*));
    for (i = 0; i <= M; i = i + 1)
    {
        result[i] = malloc((M + 1) * sizeof(int));
        for (j = 0; j <= M; j = j + 1)
            result[i][j] = -1;
    }
    getPrime(N, tmp, bool, gps, prime);
    primeCount = tmp[0];
    for (i = 1; i < primeCount; i = i + 1)
        for (j = i + 1; j <= primeCount; j = j + 1)
            if (result[i][j] == -1) {
                result[i][j] = getResult(N, i, j, bool, gps, prime, result);
                if (result[i][j] > 1) {
                    printF(prime[i], prime[j], result[i][j]);
                    resultCount = resultCount + 1;
                }
            }
    printf("Total: %d\n", resultCount);
    return 0;
}
