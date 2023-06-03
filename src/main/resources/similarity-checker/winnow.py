import numpy as np


def _winnow(hashes, window_size):
    """Implementation of the winnowing algorithm"""
    selected_idx = []

    buffer = np.full(window_size, np.inf)
    r = 0
    min_idx = 0
    for hash_idx, hash_val in enumerate(hashes):
        r = (r + 1) % window_size
        buffer[r] = hash_val

        if min_idx == r:
            i = (r - 1) % window_size
            while i != r:
                if buffer[i] < buffer[min_idx]:
                    min_idx = i
                i = (i - 1) % window_size

            selected_idx.append(hash_idx - ((r - min_idx) % window_size))
        else:
            if buffer[r] < buffer[min_idx]:
                min_idx = r
                selected_idx.append(hash_idx)

    return np.array(selected_idx, dtype=np.int64)
