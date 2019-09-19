#include <stdlib.h>

typedef struct {
    void *data;
    struct Node *next;

}Node;


typedef struct{
    int size;
    struct Node *head;    
}List;


static void AddElement(Node *element, List *l) {
    if (l->size == 0){
        l->head = element;
        l->size += 1;
    }else {
        l->head->next = element;
        l->size += 1;
    }    
}

static List *NewList(){
    List *l = sizeof(List);
    l->head = NULL;
    l->size = 0;
    return l;
}

int main(int argc, char const *argv[])
{
    List *l = NewList();
    Node *n = sizeof(Node);
    n->data = 1;
    AddElement(n,l);   
    printf(l->size);
    return 0;
}
