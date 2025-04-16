<<<<<<< HEAD
import os
import numpy as np
import torch
from torch.utils.data import Dataset
from sklearn.model_selection import train_test_split 
from torch.utils.data import DataLoader
import torch.nn as nn
from torchvision.models import resnet18

#CREATE THE CLASS DATASET THAT ITERATES WITH THE DATA LOADER 
class CSIDataset(Dataset):
    def __init__(self, directory, label_map):
        self.data_files = []
        self.labels = []

        # Iterate over each category directory and load files
        for category, label in label_map.items():
            category_dir = os.path.join(directory, category)
            for file in os.listdir(category_dir):
                if file.endswith('.npy'):
                    file_path = os.path.join(category_dir, file)
                    self.data_files.append(file_path)
                    self.labels.append(label)

    def __len__(self):
        return len(self.data_files)

    def __getitem__(self, idx):
        # Load a single data file and its label
        file_path = self.data_files[idx]
        csi_data = np.load(file_path)
        signal = torch.tensor(csi_data, dtype=torch.float32)
        
        # Ensure all data are 2D (add channel dimension if necessary)
        if len(signal.shape) == 1:
            signal = signal.unsqueeze(0)
        
        label = self.labels[idx]
        return signal, label
    
#DEFINE A COLLATE FUNCTION TO HANDLE ARRAYS AND DIMENSIONAL SIZES
def collate_fn(batch):
    data, labels = zip(*batch)
    # Calculate the maximum sizes for each dimension across all tensors in the batch
    max_size = tuple(max(sizes) for sizes in zip(*(item.shape for item in data)))
    
    # Initialize the list to hold padded data
    data_padded = []
    
    # Loop through each item and calculate padding for each, then pad it
    for item in data:
        # Calculate padding for each dimension
        padding = []
        for dim in range(len(max_size)):
            if dim < len(item.shape):
                after_pad = max_size[dim] - item.shape[dim]
            else:
                after_pad = max_size[dim]
            padding.extend([0, after_pad])  # Prepend 0 for padding before, append calculated for padding after
        
        # Reverse the padding order for PyTorch which requires right to left dimension specification
        padding = padding[::-1]
        
        # Apply padding to the current item
        padded_item = torch.nn.functional.pad(item, padding, "constant", 0)
        data_padded.append(padded_item)
    
    # Stack all padded data and convert labels to a tensor
    data_stacked = torch.stack(data_padded)
    labels_stacked = torch.tensor(labels)
    
    return data_stacked, labels_stacked

#CREATE THE DATASET AND THE DATA LOADER
# Label map for activities
label_map = {'kneel': 0, 'liedown': 1, 'pickup': 2, 'sit': 3, 'sitrotate': 4, 'stand': 5, 'standrotate': 6, 'walk': 7}

# Directory containing the data
directory = 'C:/Users/jessi/Documents/DeakinUniversity/GuardianMLgroup/T3-2023/Room2_npy'

# Initialize Dataset
dataset = CSIDataset(directory, label_map)

# Create DataLoader
dataloader = DataLoader(dataset, batch_size=10, shuffle=True, collate_fn=collate_fn)

##DEFINING MODEL
num_classes = 8
model = resnet18(pretrained=True)

# 1. Adapting the model to 1 channel
old_conv_layer = model.conv1

# 2. create a new convolutional layer with 1 input channel
# but same output channels and kernel size as the old layer
new_conv_layer = nn.Conv2d(1, old_conv_layer.out_channels, 
                           kernel_size=old_conv_layer.kernel_size, 
                           stride=old_conv_layer.stride, 
                           padding=old_conv_layer.padding, 
                           bias=old_conv_layer.bias)

# 3. Take the average of the weights across the input channels and assign it to the new layer 
new_conv_layer.weight.data = torch.mean(old_conv_layer.weight.data, dim=1, keepdim=True)

# 4. Raplace the first convolutional layer
model.conv1 = new_conv_layer

model.fc = nn.Linear(model.fc.in_features, num_classes)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = model.to(device)

#DEFINING LOSS FUNCTION AND OPTIMIZER
criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=1e-5, weight_decay=0.001)

#TRAINING THE MODEL
def train_model(model, dataloader, criterion, optimizer, num_epochs=70):
    model.train()
    for epoch in range(num_epochs):
        running_loss = 0.0
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
            #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)
        
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

            running_loss += loss.item() * inputs.size(0)
        epoch_loss = running_loss / len(dataloader.dataset)
        print(f'Epoch {epoch+1}/{num_epochs}, Loss: {epoch_loss:.4f}')

train_model(model, dataloader, criterion, optimizer)

# EVALUATE THE MODEL

def evaluate_model(model, dataloader):
    model.eval()
    correct = 0
    total = 0
    with torch.no_grad():
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
             #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)

            outputs = model(inputs)
            _, predicted = torch.max(outputs.data, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()
    print(f'Accuracy: {100 * correct / total:.2f}%')


=======
<<<<<<< HEAD
import os
import numpy as np
import torch
from torch.utils.data import Dataset
from sklearn.model_selection import train_test_split 
from torch.utils.data import DataLoader
import torch.nn as nn
from torchvision.models import resnet18

#CREATE THE CLASS DATASET THAT ITERATES WITH THE DATA LOADER 
class CSIDataset(Dataset):
    def __init__(self, directory, label_map):
        self.data_files = []
        self.labels = []

        # Iterate over each category directory and load files
        for category, label in label_map.items():
            category_dir = os.path.join(directory, category)
            for file in os.listdir(category_dir):
                if file.endswith('.npy'):
                    file_path = os.path.join(category_dir, file)
                    self.data_files.append(file_path)
                    self.labels.append(label)

    def __len__(self):
        return len(self.data_files)

    def __getitem__(self, idx):
        # Load a single data file and its label
        file_path = self.data_files[idx]
        csi_data = np.load(file_path)
        signal = torch.tensor(csi_data, dtype=torch.float32)
        
        # Ensure all data are 2D (add channel dimension if necessary)
        if len(signal.shape) == 1:
            signal = signal.unsqueeze(0)
        
        label = self.labels[idx]
        return signal, label
    
#DEFINE A COLLATE FUNCTION TO HANDLE ARRAYS AND DIMENSIONAL SIZES
def collate_fn(batch):
    data, labels = zip(*batch)
    # Calculate the maximum sizes for each dimension across all tensors in the batch
    max_size = tuple(max(sizes) for sizes in zip(*(item.shape for item in data)))
    
    # Initialize the list to hold padded data
    data_padded = []
    
    # Loop through each item and calculate padding for each, then pad it
    for item in data:
        # Calculate padding for each dimension
        padding = []
        for dim in range(len(max_size)):
            if dim < len(item.shape):
                after_pad = max_size[dim] - item.shape[dim]
            else:
                after_pad = max_size[dim]
            padding.extend([0, after_pad])  # Prepend 0 for padding before, append calculated for padding after
        
        # Reverse the padding order for PyTorch which requires right to left dimension specification
        padding = padding[::-1]
        
        # Apply padding to the current item
        padded_item = torch.nn.functional.pad(item, padding, "constant", 0)
        data_padded.append(padded_item)
    
    # Stack all padded data and convert labels to a tensor
    data_stacked = torch.stack(data_padded)
    labels_stacked = torch.tensor(labels)
    
    return data_stacked, labels_stacked

#CREATE THE DATASET AND THE DATA LOADER
# Label map for activities
label_map = {'kneel': 0, 'liedown': 1, 'pickup': 2, 'sit': 3, 'sitrotate': 4, 'stand': 5, 'standrotate': 6, 'walk': 7}

# Directory containing the data
directory = 'C:/Users/jessi/Documents/DeakinUniversity/GuardianMLgroup/T3-2023/Room2_npy'

# Initialize Dataset
dataset = CSIDataset(directory, label_map)

# Create DataLoader
dataloader = DataLoader(dataset, batch_size=10, shuffle=True, collate_fn=collate_fn)

##DEFINING MODEL
num_classes = 8
model = resnet18(pretrained=True)

# 1. Adapting the model to 1 channel
old_conv_layer = model.conv1

# 2. create a new convolutional layer with 1 input channel
# but same output channels and kernel size as the old layer
new_conv_layer = nn.Conv2d(1, old_conv_layer.out_channels, 
                           kernel_size=old_conv_layer.kernel_size, 
                           stride=old_conv_layer.stride, 
                           padding=old_conv_layer.padding, 
                           bias=old_conv_layer.bias)

# 3. Take the average of the weights across the input channels and assign it to the new layer 
new_conv_layer.weight.data = torch.mean(old_conv_layer.weight.data, dim=1, keepdim=True)

# 4. Raplace the first convolutional layer
model.conv1 = new_conv_layer

model.fc = nn.Linear(model.fc.in_features, num_classes)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = model.to(device)

#DEFINING LOSS FUNCTION AND OPTIMIZER
criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=1e-5, weight_decay=0.001)

#TRAINING THE MODEL
def train_model(model, dataloader, criterion, optimizer, num_epochs=70):
    model.train()
    for epoch in range(num_epochs):
        running_loss = 0.0
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
            #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)
        
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

            running_loss += loss.item() * inputs.size(0)
        epoch_loss = running_loss / len(dataloader.dataset)
        print(f'Epoch {epoch+1}/{num_epochs}, Loss: {epoch_loss:.4f}')

train_model(model, dataloader, criterion, optimizer)

# EVALUATE THE MODEL

def evaluate_model(model, dataloader):
    model.eval()
    correct = 0
    total = 0
    with torch.no_grad():
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
             #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)

            outputs = model(inputs)
            _, predicted = torch.max(outputs.data, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()
    print(f'Accuracy: {100 * correct / total:.2f}%')


=======
import os
import numpy as np
import torch
from torch.utils.data import Dataset
from sklearn.model_selection import train_test_split 
from torch.utils.data import DataLoader
import torch.nn as nn
from torchvision.models import resnet18

#CREATE THE CLASS DATASET THAT ITERATES WITH THE DATA LOADER 
class CSIDataset(Dataset):
    def __init__(self, directory, label_map):
        self.data_files = []
        self.labels = []

        # Iterate over each category directory and load files
        for category, label in label_map.items():
            category_dir = os.path.join(directory, category)
            for file in os.listdir(category_dir):
                if file.endswith('.npy'):
                    file_path = os.path.join(category_dir, file)
                    self.data_files.append(file_path)
                    self.labels.append(label)

    def __len__(self):
        return len(self.data_files)

    def __getitem__(self, idx):
        # Load a single data file and its label
        file_path = self.data_files[idx]
        csi_data = np.load(file_path)
        signal = torch.tensor(csi_data, dtype=torch.float32)
        
        # Ensure all data are 2D (add channel dimension if necessary)
        if len(signal.shape) == 1:
            signal = signal.unsqueeze(0)
        
        label = self.labels[idx]
        return signal, label
    
#DEFINE A COLLATE FUNCTION TO HANDLE ARRAYS AND DIMENSIONAL SIZES
def collate_fn(batch):
    data, labels = zip(*batch)
    # Calculate the maximum sizes for each dimension across all tensors in the batch
    max_size = tuple(max(sizes) for sizes in zip(*(item.shape for item in data)))
    
    # Initialize the list to hold padded data
    data_padded = []
    
    # Loop through each item and calculate padding for each, then pad it
    for item in data:
        # Calculate padding for each dimension
        padding = []
        for dim in range(len(max_size)):
            if dim < len(item.shape):
                after_pad = max_size[dim] - item.shape[dim]
            else:
                after_pad = max_size[dim]
            padding.extend([0, after_pad])  # Prepend 0 for padding before, append calculated for padding after
        
        # Reverse the padding order for PyTorch which requires right to left dimension specification
        padding = padding[::-1]
        
        # Apply padding to the current item
        padded_item = torch.nn.functional.pad(item, padding, "constant", 0)
        data_padded.append(padded_item)
    
    # Stack all padded data and convert labels to a tensor
    data_stacked = torch.stack(data_padded)
    labels_stacked = torch.tensor(labels)
    
    return data_stacked, labels_stacked

#CREATE THE DATASET AND THE DATA LOADER
# Label map for activities
label_map = {'kneel': 0, 'liedown': 1, 'pickup': 2, 'sit': 3, 'sitrotate': 4, 'stand': 5, 'standrotate': 6, 'walk': 7}

# Directory containing the data
directory = 'C:/Users/jessi/Documents/DeakinUniversity/GuardianMLgroup/T3-2023/Room2_npy'

# Initialize Dataset
dataset = CSIDataset(directory, label_map)

# Create DataLoader
dataloader = DataLoader(dataset, batch_size=10, shuffle=True, collate_fn=collate_fn)

##DEFINING MODEL
num_classes = 8
model = resnet18(pretrained=True)

# 1. Adapting the model to 1 channel
old_conv_layer = model.conv1

# 2. create a new convolutional layer with 1 input channel
# but same output channels and kernel size as the old layer
new_conv_layer = nn.Conv2d(1, old_conv_layer.out_channels, 
                           kernel_size=old_conv_layer.kernel_size, 
                           stride=old_conv_layer.stride, 
                           padding=old_conv_layer.padding, 
                           bias=old_conv_layer.bias)

# 3. Take the average of the weights across the input channels and assign it to the new layer 
new_conv_layer.weight.data = torch.mean(old_conv_layer.weight.data, dim=1, keepdim=True)

# 4. Raplace the first convolutional layer
model.conv1 = new_conv_layer

model.fc = nn.Linear(model.fc.in_features, num_classes)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = model.to(device)

#DEFINING LOSS FUNCTION AND OPTIMIZER
criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=1e-5, weight_decay=0.001)

#TRAINING THE MODEL
def train_model(model, dataloader, criterion, optimizer, num_epochs=70):
    model.train()
    for epoch in range(num_epochs):
        running_loss = 0.0
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
            #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)
        
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

            running_loss += loss.item() * inputs.size(0)
        epoch_loss = running_loss / len(dataloader.dataset)
        print(f'Epoch {epoch+1}/{num_epochs}, Loss: {epoch_loss:.4f}')

train_model(model, dataloader, criterion, optimizer)

# EVALUATE THE MODEL

def evaluate_model(model, dataloader):
    model.eval()
    correct = 0
    total = 0
    with torch.no_grad():
        for inputs, labels in dataloader:
            inputs, labels = inputs.to(device), labels.to(device)
             #Add channel dimension to the tensor shape when neccesary
            inputs = inputs.unsqueeze(1)
            # Adjust input data: repeat the channels to ensure match with the structure of the model keeping batch size, W, and H.
            inputs = inputs.repeat(1, 1, 1, 1)

            outputs = model(inputs)
            _, predicted = torch.max(outputs.data, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()
    print(f'Accuracy: {100 * correct / total:.2f}%')


>>>>>>> 25b79e1 (initial commit)
>>>>>>> 415cf9d (initial commit)
evaluate_model(model, dataloader)