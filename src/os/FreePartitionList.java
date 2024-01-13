package os;

public class FreePartitionList 
{
    private FreePartition head; // Head node of the linked list
    
    public void insertFreePartition(int startLocation, int size) 
    {
    	// Initialize the new partition to be inserted
        FreePartition newPartition = new FreePartition(startLocation, size);

        // Case 1: Inserting at the beginning of the list (used with MAX)
        if (head == null || startLocation < head.getStartLocation()) 
        {
            newPartition.setNext(head);
            head = newPartition;
        } 
        // Case 2: Inserting in the middle of the list or at the end
        else 
        {
            FreePartition current = head;
            FreePartition prev = null;

            while (current != null && startLocation >= current.getStartLocation()) 
            {
                prev = current;
                current = current.getNext();
            }

            // Insert the new partition after the 'prev' node
            prev.setNext(newPartition);
            newPartition.setNext(current);
        }

        mergeAdjacentPartitions();
    }

    private void mergeAdjacentPartitions() 
    {
        FreePartition current = head;

        while (current != null && current.getNext() != null) 
        {
            FreePartition nextPartition = current.getNext();

            // Check if the current and next partitions are adjacent
            if (current.getStartLocation() + current.getSize() >= nextPartition.getStartLocation()) 
            {
                // Merge the current and next partitions
                current.setSize(current.getSize() + nextPartition.getSize());
                current.setNext(nextPartition.getNext());
            } 
            else {
                current = current.getNext();
            }
        }
    }
    
    public void deleteFreePartition(int startLocation) 
    {
        FreePartition current = head;
        FreePartition prev = null;

        // Search for the node with the specified starting location.
        while (current != null && current.getStartLocation() != startLocation) 
        {
            prev = current;
            current = current.getNext();
        }

        // If the node is found, delete it.
        if (current != null) 
        {
            // Case 1: Node to be deleted is the head.
            if (prev == null) {
                head = current.getNext();
            } 
            // Case 2: Node to be deleted is in the middle or at the end.
            else {
                prev.setNext(current.getNext());
            }
        }
    }
    
    public FreePartition Search(int requiredSize) 
    {
        FreePartition current = head;

        while (current != null) 
        {
        	// If current partition is of sufficient size, return it.
            if (current.getSize() >= requiredSize) {
                return current;
            }
            current = current.getNext();
        }
        return null; // No suitable partition found.
    }
    
	public int Allocate(int size)
	{
		FreePartition selectedPartition = this.Search(size);
		
		// If no free partitions exist, return -1.
		if (selectedPartition == null) {
			return -1;
		}
		else
		{
			// Get the location of the first free partition found
			int processStartLocation = selectedPartition.getStartLocation();
			// Use it to calculate the new location and size after this process is allocated
			int newStartLocation = selectedPartition.getStartLocation() + size;
			int newSize = selectedPartition.getSize() - size;
			// Delete the original free partition
			this.deleteFreePartition(processStartLocation);
			// Insert the modified free partition, if it now still exists
			if (newSize != 0) {
				this.insertFreePartition(newStartLocation, newSize);
			}
			// Return the location of the original partition, this now serves as the location of the allocated process
			return selectedPartition.getStartLocation();
		}
	}
	
	public void Deallocate(int startingLocation, int size) {
		this.insertFreePartition(startingLocation, size);
	}
    
    public void printFreePartitionList() 
    {
        FreePartition current = head;
    	System.out.print("Free partition space(s): ");
        while (current != null) 
        {
            System.out.print("|" + current.getStartLocation() + " - " + (current.getStartLocation() + current.getSize()) + "|");
            current = current.getNext();
        }
        System.out.println("");
    }
}
