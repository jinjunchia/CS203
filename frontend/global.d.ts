interface User {
	id: string;
	email: string;
	username: string;
	userType: string;
}

interface AuthContextType {
	user: User | null;
	loading: boolean;
	login: (username: string, password: string) => Promise<void>;
	logout: () => void;
}