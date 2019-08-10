#ifndef _LCORE_ARCH_H
#define _LCORE_ARCH_H

/*
 * machine params
 */
#define MACHINE_MMSIZE		(16 * 1024 * 1024)		// 16MB
#define MACHINE_SDSIZE		(1 * 1024 * 1024 * 1024)// 1GB

extern unsigned int get_phymm_size();
extern unsigned int get_sd_size();

/*
 * IO address space
 */
#define ROM_START			0xff000000

#define IO_RESERVE			0xffff0000

#define IO_VGA				0xffff0100
#define _IO_VGA_CTRL		0xffff0100
#define _IO_VGA_BUFF		0xffff0104
#define _IO_VGA_CURS		0xffff0108
#define _IO_VGA_FLASH		0xffff010c

#define IO_KEYB				0xffff0300
#define _IO_KEYB_STATUS		0xffff0300
#define _IO_KEYB_RESR1		0xffff0304
#define _IO_KEYB_RESR2		0xffff0308
#define _IO_KEYB_DATA		0xffff030c

#define IO_SPI				0xffff0500
#define _IO_SPI_SPISTATUS	0xffff0500
#define _IO_SPI_BUFFSTATUS	0xffff0504
#define _IO_SPI_CTRL		0xffff0508
#define _IO_SPI_DATA		0xffff050c

/*
 * interrupt
 */
#define EXCEPT_ENRTY		0x0

#endif

