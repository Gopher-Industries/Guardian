//Needs to be integrated with Backend API once the CORS issue is solved.

// import api from "./api";

// export async function loginAdmin({ email, password }) {
//   const response = await api.post("/auth/login", {
//     email,
//     password,
//   });

//   return response.data;
// }

// export async function sendPin(email) {
//   const response = await api.post("/auth/send-pin", {
//     email,
//   });

//   return response.data;
// }

// export async function verifyPin({ email, otp }) {
//   const response = await api.post("/auth/verify-pin", {
//     email,
//     otp,
//   });

//   return response.data;
// }

// Mock login and otp fallback
import api from "./api";

export async function loginAdmin({ email, password }) {
  const response = await api.post("/auth/login", {
    email,
    password,
  });
  console.log("LOGIN RESPONSE:", response.data); 
  return response.data;
}

export async function sendPin(email) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        message: "OTP functionality is temporarily disabled for testing.",
      });
    }, 500);
  });
}

export async function verifyPin({ email, otp }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (otp === "123456") {
        resolve({
          message: "OTP verification bypassed for testing.",
        });
      } else {
        reject(new Error("Invalid OTP"));
      }
    }, 700);
  });
}