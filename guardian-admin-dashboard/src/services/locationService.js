import api from "./api";
 
export async function getLocations() {
    const response = await api.get("/locations");
    return response.data;

}
 
export async function getLocationById(id) {
    const response = await api.get(`/locations/${id}`);
    return response.data;

}
 
export async function createLocation(locationData) {
    const response = await api.post("/locations", locationData);
    return response.data;

}
 
export async function updateLocation(id, locationData) {
    const response = await api.patch(`/locations/${id}`, locationData);
    return response.data;

}
 
export async function deleteLocation(id) {
    const response = await api.delete(`/locations/${id}`);
    return response.data;

}
 


 