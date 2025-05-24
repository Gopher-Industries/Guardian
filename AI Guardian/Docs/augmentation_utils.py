import torch
import torch.nn.functional as F
import random

class RandomCircularRotation:
    """
    Simulates time shifts by circularly rolling the signal along the time axis.
    """
    def __init__(self, p=0.5):
        self.p = p

    def __call__(self, x):
        if random.random() > self.p:
            return x
        shift = random.randint(1, x.shape[1] - 1)
        return torch.roll(x, shifts=shift, dims=1)


class RandomResizedCrop:
    """
    Crops a random portion of the signal and resizes it back to original width using 1D interpolation.
    """
    def __init__(self, p=0.5):
        self.p = p

    def __call__(self, x):
        if random.random() > self.p:
            return x

        original_width = x.shape[1]
        crop_width = random.randint(original_width // 2, original_width)
        start = random.randint(0, original_width - crop_width)
        cropped = x[:, start:start + crop_width]  # shape (C, crop_width)

        # Use 1D interpolation: reshape to (N=1, C, W)
        cropped = cropped.unsqueeze(0)
        resized = F.interpolate(cropped, size=original_width, mode='linear', align_corners=False)
        return resized.squeeze(0)  # back to (C, W)


class RandomAmplitude:
    """
    Randomly scales each value to simulate signal strength variation.
    """
    def __init__(self, p=0.5, scale_range=(0.75, 1.25)):
        self.p = p
        self.scale_range = scale_range

    def __call__(self, x):
        if random.random() > self.p:
            return x
        scale = torch.empty_like(x).uniform_(*self.scale_range)
        return x * scale


class RandomContrast:
    """
    Adjusts contrast by stretching/compressing values around the mean per channel.
    """
    def __init__(self, p=0.5, scale_range=(0.75, 1.25)):
        self.p = p
        self.scale_range = scale_range

    def __call__(self, x):
        if random.random() > self.p:
            return x
        contrast = torch.empty((x.shape[0], 1)).uniform_(*self.scale_range)
        mean = x.mean(dim=1, keepdim=True)
        return (x - mean) * contrast + mean


class CombinedAugmentation:
    """
    Applies all transformations in sequence, each with the same probability `p`.
    """
    def __init__(self, p=0.5):
        self.transforms = [
            RandomCircularRotation(p),
            RandomResizedCrop(p),
            RandomAmplitude(p),
            RandomContrast(p)
        ]

    def __call__(self, x):
        for t in self.transforms:
            x = t(x)
        return x