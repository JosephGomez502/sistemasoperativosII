export interface Airport { id: number; name: string; city: string; country: string; iataCode: string; }
export interface Aircraft { id: number; model: string; capacity: number; airline: string; }
export interface Flight {
  id: number; origin: Airport; destination: Airport; aircraft: Aircraft;
  departureTime: string; arrivalTime: string; price: number; availableSeats: number; status: string;
}
export interface Seat { id: number; seatNumber: string; reserved: boolean; }
export interface Reservation { id: number; code: string; flight: Flight; seat: Seat; status: string; createdAt: string; }
export interface AuthResponse { accessToken: string; refreshToken: string; role: 'ADMIN' | 'CLIENT'; fullName: string; }
export interface Dashboard { sales: number; activeFlights: number; users: number; confirmedReservations: number; }
