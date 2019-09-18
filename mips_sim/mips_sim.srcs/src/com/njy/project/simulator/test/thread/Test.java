package com.njy.project.simulator.test.thread;

public class Test {
	public static void main(String[] args) {
		Buffer buffer = new Buffer();

		// consume
		new Thread(new Consumer(buffer)).start();
		new Thread(new Consumer(buffer)).start();

		// produce
		new Thread(new Producer(buffer)).start();
	}
}

class Buffer {
	private static final int BUFFER_SIZE = 16;
	private int[] buffer = new int[BUFFER_SIZE];
	private int readPtr = 0;
	private int writePtr = 0;
	private int size = 0;

	public int read() {
		return read(1);
	}

	public synchronized int read(int a) {
		int data = 0;

		if (size > 0) {
			size--;
			data = buffer[readPtr++];
			if (readPtr >= BUFFER_SIZE) {
				readPtr -= BUFFER_SIZE;
			}
		}

		return data;
	}

	public synchronized void write(int data) {
		if (size == BUFFER_SIZE) {
			System.out.println("FULL!");
		} else {
			buffer[writePtr++] = data;
			size++;
			if (writePtr >= BUFFER_SIZE) {
				writePtr -= BUFFER_SIZE;
			}
		}
	}
}

class Producer implements Runnable {
	public Producer(Buffer buffer) {
		// TODO Auto-generated constructor stub
		this.buffer = buffer;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			int i = 1;
			while (true) {
				Thread.sleep(2000);
				buffer.write(i++);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Buffer buffer;
}

class Consumer implements Runnable {
	public Consumer(Buffer buffer) {
		// TODO Auto-generated constructor stub
		this.buffer = buffer;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				Thread.sleep(1000);
				System.out.println(buffer.read());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Buffer buffer;
}