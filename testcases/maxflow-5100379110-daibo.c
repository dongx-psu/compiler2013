#include <stdio.h>

int c[110][110];
int ans;
int visit[110], pre[110], f[110];
int i, j, open, closed;

int build(int start, int ending) {
    for (i = 1; i <= 49; i++) {
        for (j = 50; j <= 98-i+1; j++) {
            c[i][j] = 1;
        }
    }
    for (i = 1; i <= 49; i++)
        c[start][i] = 1;
    for (i = 50; i <= 98; i++)
        c[i][ending] = 1;
    return 0;
}

int find(int ending, int start, int flag) {
    open = 0;
    closed = 1;
    for (i = 1; i <= ending; i++) {
        visit[i] = 0;
    }
    f[1]=start;
    visit[start]=1;
    pre[start]=0;
    flag=0;
    while (open<closed && flag==0) {
        open++;
        i=f[open];
        for (j = 1; j <= ending; j++)
            if (c[i][j]>0 && visit[j]==0) {
                visit[j]=1;
                closed++;
                f[closed]=j;
                pre[j]=i;
                if (closed==ending) flag=1;
            }
    }
    return flag;
}

int improve(int ending) {
    i=ending;
    ans++;
    while (pre[i]>0) {
        j=pre[i];
        c[j][i]--;
        c[i][j]++;
        i=j;
    }
    return 0;
}

int main() {
    int k, start, ending;
    int flag;
    int i1;

    k = 0;
    start = 99;
    ending = 100;
    flag = 0;

    build(start, ending);
    while (find(ending, start, flag)>0) {
        improve(ending);
    }
    printf("%d \n", ans);  	/*  ans is 49  */
    return 0;
}

