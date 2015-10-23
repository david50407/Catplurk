package tw.catcafe.catplurk.android.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Davy
 */
public class NoDuplicatesArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = -4261317003275081311L;

    public NoDuplicatesArrayList() {
    }

    public NoDuplicatesArrayList(final Collection<? extends E> collection) {
        addAll(collection);
    }

    public NoDuplicatesArrayList(final int capacity) {
        super(capacity);
    }

    @Override
    public boolean add(final E e) {
        if (contains(e))
            return false;
        else
            return super.add(e);
    }

    @Override
    public void add(final int index, final E element) {
        if (!contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        final Collection<E> copy = new ArrayList<>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> collection) {
        final Collection<E> copy = new ArrayList<>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }
}
