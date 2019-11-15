package game;

import java.util.Iterator;

class Node<E>
{
	E data;
	Node<E> next;
	public Node(E data)
	{
		this.data = data;
		this.next = null;
	}
}
public class CustomList<E> implements Iterable<E>{

	Node<E> head, tail;
	int size;
	public CustomList()
	{
		head = null;
		tail = null;
		size = 0;
	}
	public void add(E data)
	{
		if(head == null)
		{
			tail = new Node<E>(data);
			head = tail;
		}
		else
		{
			tail.next = new Node<E>(data);
			tail = tail.next;
		}
		size++;
	}
	public void removeHead()
	{
		Node<E> tmp;
		tmp = head;
		head = head.next;
		tmp.next = null;
		size--;
	}
	public Node<E> getHead()
	{
		return head;
	}
	public int size()
	{
		return size;
	}
	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new CustomIterator<E>(this);
	}
}
class CustomIterator<E> implements Iterator<E>
{
	Node<E> cursor;

	public CustomIterator(CustomList<E> list)
	{
		cursor = list.head;
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return cursor != null;
	}

	@Override
	public E next() {
		// TODO Auto-generated method stub
		E data = cursor.data;
		cursor = cursor.next;
		return data;
	}
	
}

