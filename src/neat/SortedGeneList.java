package neat;

import java.util.Iterator;


class listNode
{
	Gene data;
	listNode next;
	public listNode(Gene data)
	{
		this.data = data;
		this.next = null;
	}
}

/*Specifically used to store Genes in the genome in a sorted manner*/
public class SortedGeneList {


	
	listNode head, tail;
	int size;
	
	public SortedGeneList()
	{
		this.head = null;
		this.tail = null;
		this.size = 0;
	}
	/*Adding elements in a sorted manner*/
	public void add(Gene gene)
	{
		this.size++;
		if(this.head == null)
		{
			this.head = new listNode(gene);
			this.tail = head;
		}
		else
		{
			listNode tmp, nextNode;
			tmp = this.head;
			while(tmp != null)
			{
				if(gene.getInovationNumber() < tmp.data.getInovationNumber())
				{
					nextNode = tmp.next;
					tmp.next = new listNode(tmp.data);
					tmp.data = gene;
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
				tail.next = new listNode(gene);
				tail  = tail.next;
			}
		}
	}
	public void remove(int index) throws IndexOutOfBoundsException
	{
		if(this.size <= index)
		{
			throw new IndexOutOfBoundsException();
		}
		listNode tmp = this.head, prev = null;
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
	public Iterator<Gene> iterator()
	{
		return new SortedListIterator(this);
	}
	public void prinList()
	{
		listNode tmp;
		tmp = this.head;
		while(tmp != null)
		{
			System.out.print(tmp.data.getInovationNumber() + " ");
			tmp = tmp.next;
		}
		System.out.println();
	}
}

class SortedListIterator implements Iterator<Gene>
{
	listNode cursor;

	public SortedListIterator(SortedGeneList list)
	{
		cursor = list.head;
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return cursor != null;
	}

	@Override
	public Gene next() {
		// TODO Auto-generated method stub
		Gene data = cursor.data;
		cursor = cursor.next;
		return data;
	}
	
}