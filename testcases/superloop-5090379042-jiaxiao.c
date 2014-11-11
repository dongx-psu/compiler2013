/*
  Calculator of 6!
  @author yixi
*/
#include <stdio.h>

int main() {
  int N, h, i, j, k, total, a, b, c, d, e, f;
	N = 6;
	h = 99;
	i = 100;
	j = 101;
	k = 102;
	total = 0;
	for ( a=1; a<=N; a++ )
	for ( b=1; b<=N; b++ )
	for ( c=1; c<=N; c++ )	
	for ( d=1; d<=N; d++ )
	for ( e=1; e<=N; e++ )
	for ( f=1; f<=N; f++ )
		if (a!=b && a!=c && a!=d && a!=e && a!=f && a!=h && a!=i && a!=j && a!=k
              && b!=c && b!=d && b!=e && b!=f && b!=h && b!=i && b!=j && b!=k
              && c!=d && c!=e && c!=f && c!=h && c!=i && c!=j && c!=k
              && d!=e && d!=f && d!=h && d!=i && d!=j && d!=k
              && e!=f && e!=h && e!=i && e!=j && e!=k
              && f!=h && f!=i && f!=j && f!=k && i!=j && h!=k)
		{
			total++;
		}
	
	printf("%d\n", total);
	return 0;
}
