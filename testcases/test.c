#include <stdio.h>

int strlen(char *str)
{
    int i;
    for(i=0; str[i]!=0; i++);
    return i;
}

int find(int s, char *str)
{
    int cnt, i;
    
    cnt = 1;
    i = s + 1;
    
    while(cnt > 0)
    {
        if(str[i] == '(')
        {
            cnt = cnt + 1;
        }
        else if(str[i] == ')')
        {
            cnt = cnt - 1;
        }
        i = i + 1;
    }
    return i - 1;
}

int calc(int s, int e, char *str)
{
    int ret, i, j, first, second;
    ret = 0;
    i = s;
    j = 0;
    first = second = -1;
    
    if(s == e)
    {
        ret = str[s] - '0';
    }
    else
    {
        while(i < e)
        {
            if(str[i] == '(')
            {
                i = find(i, str);
            }
            else if(str[i] == '*' || str[i] == '/')
            {
                first = i;
                i = i + 1;
            }
            else if(str[i] == '+' || str[i] == '-')
            {
                second = i;
                i = i + 1;
            }
            else
            {
                i = i + 1;
            }
        }
        if(second >= 0)
        {
            if(str[second] == '+')
            {
                ret = calc(s, second-1, str) + calc(second+1, e, str);
            }
            else
            {
                ret = calc(s, second-1, str) - calc(second+1, e, str);
            }
        }
        else if(first >= 0)
        {
            if(str[first] == '*')
            {
                ret = calc(s, first-1, str) * calc(first+1, e, str);
            }
            else
            {
                ret = calc(s, first-1, str) / calc(first+1, e, str);
            }
        }
        else
        {
            ret = calc(s+1, e-1, str);
        }
    }
    return ret;
}

int main()
{
    char *str;
    str = "1+3+5+7+9*2";
    printf("%d\n", calc(0, strlen(str)-1, str));
    str = "8/4*3-(5+9-4)";
    printf("%d\n", calc(0, strlen(str)-1, str));
    str = "7+8*(3+2*4)";
    printf("%d\n", calc(0, strlen(str)-1, str));
    return 0;
}