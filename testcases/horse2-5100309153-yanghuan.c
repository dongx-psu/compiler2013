#include <stdio.h>

int check(int a, int N) {
    return ((a < N) && (a >= 0));
}

int main() {
    int N;
    int head, startx, starty;
    int targetx, targety, tail, ok, now;
    int x, y;
    int *xlist, *ylist;
    int **step;
    int i, j;
    
    N = 100;
    head = tail = startx = starty = 0;
    targetx = targety  = N - 1;
    x = y = 0;
    now = ok = 0;
    xlist = malloc(N * N * sizeof(int));
    for (i = 0; i < N * N; i = i + 1)
        xlist[i] = 0;
    ylist = malloc(N * N * sizeof(int));
    for (i = 0; i < N * N; i = i + 1)
        ylist[i] = 0;
    step = malloc(N * sizeof(int*));
    for (i = 0; i < N; i =  i + 1) {
        step[i] = malloc(N * sizeof(int));
        for (j = 0; j < N; j = j + 1)
        step[i][j] = -1;
    
    }
    xlist[0] = startx;
    ylist[0] = starty;
    step[startx][starty] == 0;
    while (head <= tail)
    {
        now = step[xlist[head]][ylist[head]];
        x = xlist[head] - 1;
        y = ylist[head] - 2;
        if (check(x, N) == 1 && check(y, N) == 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] - 1;
        y = ylist[head] + 2;
        if (check(x, N) == 1 && check(y, N) == 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] + 1;
        y = ylist[head] - 2;
        if (check(x, N) == 1 && check(y, N) == 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] + 1;
        y = ylist[head] + 2;
        if (check(x, N) == 1 && check(y, N) == 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] - 2;
        y = ylist[head] - 1;
        if (check(x, N) == 1 && check(y, N)== 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] - 2;
        y = ylist[head] + 1;
        if (check(x, N) == 1 && check(y, N)== 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] + 2;
        y = ylist[head] - 1;
        if (check(x, N) == 1 && check(y, N)== 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        x = xlist[head] + 2;
        y = ylist[head] + 1;
        if (check(x, N) == 1 && check(y, N) == 1 && step[x][y] == -1)
        {
            tail = tail + 1;
            xlist[tail] = x;
            ylist[tail] = y;
            step[x][y] = now + 1;
            if (x == targetx && y == targety) ok = 1;
        }
        if (ok == 1) break;
        head = head + 1;
    }
    if (ok == 1) printf("%d", step[targetx][targety]);
    else printf("no solution!");
    return 0;
}