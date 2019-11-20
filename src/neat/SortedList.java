package neat;

import java.util.Comparator;
import java.util.Iterator;


class listNode<E>
{
	E data;
	listNode<E> next;
	public listNode(E data)
	{
		this.data = data;
		this.next = null;
	}
}

/*Specifically used to store Genes in the genome in a sorted manner*/
public class SortedList<E> {


	
	private listNode<E> head, tail;
	private int size;
	private Comparator<E> comparaTor;
	
	public SortedList(Comparator<E> comparaTor)
	{
		this.head = null;
		this.tail = null;
		this.size = 0;
		this.comparaTor = comparaTor;
	}
	/*Adding elements in a sorted manner*/
	public void add(E data)
	{
		this.size++;
		if(this.head == null)
		{
			this.head = new listNode<E>(data);
			this.tail = head;
		}
		else
		{
			listNode<E> tmp, nextNode;
			tmp = this.head;
			while(tmp != null)
			{
				if(comparaTor.compare(data, tmp.data) < 0)
				{
					nextNode = tmp.next;
					tmp.next = new listNode<E>(tmp.data);
					tmp.data = data;
					if(tail == tmp)
						tail = tail.next;
					tmp = tmp.next;
					tmp.next = nextNode;
					return;
				}
				tmp = tmp.next;
			}
			if(tmp == null)
			{
				tail.next = new listNode<E>(data);
				tail  = tail.next;
			}
		}
	}
	public listNode<E> getHead()
	{
		return this.head;
	}
	public listNode<E> getTail()
	{
		return this.tail;
	}
	public void removeFromIndexToEnd(int fromIndex) throws IndexOutOfBoundsException
	{
		if(this.size <= fromIndex)
			throw new IndexOutOfBoundsException();
		listNode<E> tmp = this.head, prev = null, aux;
		for(int count = 0; count < fromIndex; count++)
		{
			prev = tmp;
			tmp = tmp.next;
		}
		if(prev != null)
			prev.next = null;
		else
			this.head = null;
		while(tmp != null)
		{
			aux = tmp.next;
			tmp.next = null;
			tmp = aux;
			this.size--;
		}
		this.tail = prev;
	}
	public void remove(int index) throws IndexOutOfBoundsException
	{
		if(this.size <= index)
			throw new IndexOutOfBoundsException();
		listNode<E> tmp = this.head, prev = null;
		for(int count = 0; count < index; count++)
		{
			prev = tmp;
			tmp = tmp.next;
		}
		if(prev == null)
			head = head.next;
		else
			prev.next = tmp.next;
		if(tmp == tail)
			tail = prev;
		tmp.next = null;
		this.size--;
	}
	public SortedListIterator<E> iterator()
	{
		return new SortedListIterator<E>(this);
	}
	
	public int size()
	{
		return this.size;
	}
	public E getFirstElement()
	{
		return this.head.data;
	}
	public E getData(int index) throws IndexOutOfBoundsException
	{
		if(index >= size)
		{
			throw new IndexOutOfBoundsException();
		}
		listNode<E> tmp = head;
		for(int count = 0; count < index; count++)
		{
			tmp = tmp.next;
		}
		return tmp.data;
	}
}

class SortedListIterator<E> implements Iterator<E>
{
	listNode<E> cursor;

	public SortedListIterator(SortedList<E> list)
	{
		cursor = list.getHead();
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return cursor != null;
	}
	public E getDataAtCurrentNode()
	{
		return cursor.data;
	}
	@Override
	public E next() {
		// TODO Auto-generated method stub
		E data = cursor.data;
		cursor = cursor.next;
		return data;
	}
}