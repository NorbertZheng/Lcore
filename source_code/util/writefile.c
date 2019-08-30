#include <stdio.h>
#include <stdlib.h>

unsigned int main(unsigned int argc, unsigned char **argv)
{
	FILE *fp;
	unsigned int value;

	fp = fopen(argv[1], "rb+");
	if (!fp) {
		printf("fopen failed!\n");
		return 1;
	}

	value = atoi(argv[2]);
	printf("%d ", value);
	fseek(fp, 4, SEEK_SET);
	fwrite(&value, sizeof(unsigned int), 1, fp);

	value = atoi(argv[3]);
	fseek(fp, 8, SEEK_SET);
	fwrite(&value, sizeof(unsigned int), 1, fp);

	fclose(fp);

	return 0;
}

