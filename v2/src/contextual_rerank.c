#include <stdio.h>
#include <stdlib.h>
#include "rank.c"

/**
*   Build an image's kNN given its amount of neightbors.

*   Params: 
*   K: amount of neighbors
*   filter: Since that we are computing the knn of an image regarding
*   an other image, we need to filter both of them from the kNN set
*   that is going to be built. Therefore 
**/
int *build_kNN(int K, int imgI, int imgJ, Rank r) {
    int *kNN = malloc(K*sizeof(int));
    int pos = 0;
    int aux = 0;
    if (r.entries[aux].id == imgI) {
        aux++;
    }
    while (pos < K){
        int tmp = r.entries[aux].id;
        if (tmp == imgJ){
            aux++;
        }
        kNN[pos++] = r.entries[aux++].id;
    }
    return kNN;
}

int main(int argc, char const *argv[])
{
    FILE *pointer;
    pointer = fopen("../../Contextual/resources/images/16672.jpg.ppm.distbin", "r");
    if (!pointer)
    {
        printf("Unable to access \n");
    }
    else
    {
        Distbin *distbin = NewDistbin(pointer, distbin);
        printf("˜here we go again˜\n");
        Rank r = NewRank(distbin);
        for (int i = 0; i < 8; i++)
        {
            printf("Distance:%lf\n", r.entries[i].distance);
            printf("Id: %d\n", r.entries[i].id);
        }
        int *knn = build_kNN(7,16672,9739,r);
        for (int i = 0; i < 7; i++)
        {
            printf("Id knn[%d](img 16672): %d\n",i,knn[i]);
        }
        

    }
    return 0;
}
