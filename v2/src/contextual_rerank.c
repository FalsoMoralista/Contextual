#include <stdio.h>
#include <stdlib.h>
#include "linked_list.c"


/**
*   Build an image's kNN given its amount of neightbors.

*   Params: 
*   K: amount of neighbors
*   filter: Since that we are computing the knn of an image regarding
*   an other image, we need to filter both of them from the kNN set
*   that is going to be built. Therefore 
**/
int build_kNN(int K, int filter, float tensor[]){
    int kNN[K];
    int pos = 0;
    int aux = 1;
    while (pos<K){
        

    }
    
    return kNN;    
}
int main(int argc, char const *argv[])
{
    List *l  = NewList();
    l->AddElement(10.01,l);
    print(l);
    printf("ola gente bon diah\n");
    return 0;
}
