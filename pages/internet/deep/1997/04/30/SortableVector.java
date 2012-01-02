public class SortableVector extends java.util.Vector {

	public void sort() {
		Sortable[] v = new Sortable[elementCount];
		System.arraycopy(elementData, 0, v, 0, elementCount);
		QuickSort.sort(v);
		System.arraycopy(v, 0, elementData, 0, elementCount);
	}
}
