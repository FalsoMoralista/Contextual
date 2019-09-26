#include <stdio.h>
#include <stdlib.h>
#include "rank.c"
#include "math.h"

/**
*   Build an image's kNN given its amount of neightbors.

*   Params: 
*   K: amount of neighbors
*   filter: Since that we are computing the knn of an image regarding
*   an other image, we need to filter both of them from the kNN set
*   that is going to be built. Therefore 
**/
int *buildKNN(int K, int imgI, int imgJ, Rank r)
{
    int *kNN = malloc(K * sizeof(int));
    int pos = 0;
    int aux = 0;
    if (r.entries[aux].id == imgI)
    {
        aux++;
    }
    while (pos < K)
    {
        int tmp = r.entries[aux].id;
        if (tmp == imgJ)
        {
            aux++;
        }
        kNN[pos++] = r.entries[aux++].id;
    }
    return kNN;
}

Rank *rerank(Distbin distances[20180])
{
    Rank *ranks = malloc(180 * sizeof(Rank));
    for (int i = 0; i < 180; i++)
    {
        ranks[i] = NewRank(&distances[i]);
    }
    return ranks;
}

int *contextual_rerank(Distbin a[20180], Rank *r, int K)
{
    Distbin *a_line = malloc(180 * sizeof(Distbin));
    for (int i = 0; i < 180; i++)
    {
        for (int j = 0; j < 20180; j++)
        {
            a_line[i].distances[j] = a[i].distances[j];
        }
    }

    for (int imgI = 20000; imgI < 20180; imgI++)
    { // for each imgI
        for (int imgL = 0; imgL < 20000; imgL++)
        { // for each imgL (topics rows)
            if (imgI != imgL)
            { // discard itself
                Rank rank = r[imgI - 20000];
                int *kNN = buildKNN(K, imgI, imgL, rank);
                int ck = 0;
                double dj = 0;
                for (int j = 0; j < K; j++)
                { // for each imgJ((KNN)L) do : weighted sum of distance from imgI neighbors, to imgL
                    int imgJ = kNN[j];
                    dj = dj + a[imgJ].distances[imgL] * (K - ck);
                    ck++;
                }
                double di = a[imgI].distances[imgL] / K;
                dj = dj / (K * (K + 1) / 2);
                di = di * di;
                dj = dj * dj;
                a_line[imgI - 20000].distances[imgL] = sqrt(di + dj); // recalculate distance from imgI to imgL
            }
        }
    }

    for (int i = 20000; i < 20180; i++) //Overwrite previous values for the original distances
    {
        for (int j = 0; j < 20180; j++)
        {
            a[i].distances[j] = a_line[i - 20000].distances[j];
        }
    }
    return a_line;
}

void ContextualRerank(Distbin original_distances[20180], int Ks, int Ke)
{
    int K = Ks;
    while (K < Ke)
    {
        Rank *ranked_lists = rerank(original_distances);
        original_distances = contextual_rerank(original_distances, ranked_lists, K);
        K++;
    }
}

int main(int argc, char const *argv[])
{
    printf("Sizeof argv: %d\n", argc);
    if (sizeof(argv) < 2)
    {
        printf("Usage: ./contextual [descriptor_id] [Ks] [Ke]\n");
    }
    else
    {
        int arg0 = atoi(argv[1]);
        int arg1 = atoi(argv[2]);
        int arg2 = atoi(argv[3]);
    }

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
        int *knn = buildKNN(7, 16672, 9739, r);
        for (int i = 0; i < 7; i++)
        {
            printf("Id knn[%d](img 16672): %d\n", i, knn[i]);
        }
    }
    return 0;
}