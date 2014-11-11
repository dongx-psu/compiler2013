#include <stdio.h>

void manyArguments(int a, int b, int c, int d, int e, int f)
{
    printf("%d ",a);
    printf("%d ",b);
    printf("%d ",c);
    printf("%d ",d);
    printf("%d ",e);
    printf("%d\n",f);
}
int main()
{
    manyArguments(0, 1, 2, 3, 4, 5 + 6);
    manyArguments(0, 1, 2, 3 + 4 + 5, 4, 5 - 6);
    return 0;
}

