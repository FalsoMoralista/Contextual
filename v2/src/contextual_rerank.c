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
int *build_kNN(int K, int imgI, int imgJ, Rank r)
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

void ContextualRerank(Distbin original_distances[20180], Rank *ranked_lists, int Ks, int Ke)
{
    int K = Ks;
    while (K < Ke)
    {
        for (int imgI = 20000; imgI < 20180; imgI++)
        { // for each imgI

            for (int imgL = 0; imgL < 20000; imgL++)
            { // for each imgL (topics rows)

                if (imgI != imgL)
                { // discard itself

                    //                String currentIMG = clef.getProperty(Integer.toString(i)); // get the current image

                    //                File f = new File(DATA_DIRECTORY + dScriptor + "/" + currentIMG + EXT);

                    //                Distbin distbin = new Distbin(COLLECTION_SIZE - 180, f);

                    Rank rank = ranked_lists[imgI - 20000];

                    int kNN = buildKNN(K,imgI,imgL, rank);

                    int ck = 0;
                    double dj = 0;
                    for (int j = 0; j < K ; j++)
                    { // for each imgJ((KNN)L) do : weighted sum of distance from imgL neighbors, to imgI
                        dj = dj + original_distances[imgL].distances[j] * (K - ck);
                        ck++;
                    }
                    double di = original_distances[imgI].distances[imgL] / K;
                    dj = dj / (K * (K + 1) / 2);
                    di = Math.pow(di, 2);
                    dj = Math.pow(dj, 2);

                    topics[i][l - 20000] = Math.sqrt(di + dj); // recalculate distance from imgI to imgL
                }
            }
        }
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
            int *knn = build_kNN(7, 16672, 9739, r);
            for (int i = 0; i < 7; i++)
            {
                printf("Id knn[%d](img 16672): %d\n", i, knn[i]);
            }
        }
        return 0;
    }
