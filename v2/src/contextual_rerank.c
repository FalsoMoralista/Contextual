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
// int build_kNN(int K, int filter, float tensor[]){
//     int kNN[K];
//     int pos = 0;
//     int aux = 1;
//     while (pos<K){
//     }    
//     return kNN;    
// }

int main(int argc, char const *argv[]) {
    // List *l  = NewList();
    // l->AddElement(10.01,l);
    // l->AddElement(10.02,l);
    // print(l);
    // printf("ola gente bon diah\n");
    FILE *pointer;
    pointer = fopen("../../Contextual/resources/images/16672.jpg.ppm.distbin","r");
    if(!pointer){
        printf("Unable to access \n");
    }else
    {
    
    double *dataset[180][20180] = (double*)malloc(sizeof(double [180][20180]));

        for (int image = 0; image < 1; image++) {
            for (int line = 0; line < 20180; line++) {
                char bytes[8];
                fread(&bytes, sizeof(bytes), line+1, pointer);
                double d = *((double*)bytes);
                printf("%lf\n",d);
//               dataset[image][line] = d;
            }
        }    

        // for (int image = 0; image < 1; image++) {
        //     for (int line = 0; line < 11; line++) {
        //         printf("%lf\n",dataset[image][line]);
        //     }
        // }       

    }    
    return 0;
}
