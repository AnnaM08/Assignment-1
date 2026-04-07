package pcd.sketch02.util;

import pcd.startingPoool.controller.Cmd;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * 
 * Simple implementation of a bounded buffer
 * as a monitor, using raw mechanisms
 * 
 */
public class OrderedBoundedBufferImpl implements BoundedBuffer<Cmd> {

	private final LinkedList<Cmd> buffer;
	private final int maxSize;

	public OrderedBoundedBufferImpl(int size) {
		buffer = new LinkedList<>();
		maxSize = size;
	}

	public synchronized void put(Cmd item) throws InterruptedException {
		while (isFull()) {
			wait();
		}
		buffer.addLast(item);
		notifyAll();
	}

	public synchronized Cmd get() throws InterruptedException {
		while (isEmpty()) {
			wait();
		}
		Cmd item = buffer.stream()
				.min(Comparator.comparing(Cmd::getTimestamp))
				.orElseThrow();
		buffer.remove(item);
		notifyAll();
		return item;
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.isEmpty();
	}
}
