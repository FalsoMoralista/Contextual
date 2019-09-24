#include <stdio.h>
#include <stdlib.h>
#include "linked_list.c"
#include "distbin.c"
#include "rank.c"

/**
*   Build an image's kNN given its amount of neightbors.

*   Params: 
*   K: amount of neighbors
*   filter: Since that we are computing the knn of an image regarding
*   an other image, we need to filter both of them from the kNN set
*   that is going to be built. Therefore 
**/
// int build_kNN(int K, int filter, float tensor[]){
//     int kNN[K];
//     int pos = 0;
//     int aux = 1;
//     while (pos<K){
//     }    
//     return kNN;    
// }

int main(int argc, char const *argv[]) {
    FILE *pointer;
    pointer = fopen("../../Contextual/resources/images/16672.jpg.ppm.distbin","r");
    if(!pointer){
        printf("Unable to access \n");
    }else
    {
        Distbin *distbin = NewDistbin(pointer, distbin);
        Rank *r = NewRank(distbin);
        for (int i = 0; i < 11; i++){
            printf("Val: %lf\n",r->rank[i]->distance);
        }    
    }    
    return 0;
}
