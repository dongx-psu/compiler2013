int main()
{
    int a[3][3],b[3][3],c[3][3];
    int i,j,k;
    
    for(i=0;i<3;i++)
    {
        for(j=0;j<3;j++)
        {
            for(k=0;k<3;k++)
            {
                c[i][k] += a[i][j]*b[j][k];
            }
        }
    }
    return 0;
}
