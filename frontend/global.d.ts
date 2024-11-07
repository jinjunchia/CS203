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

// Tournament
interface Tournament {
  id: number;
  name: string;
  startDate: string;
  endDate: string | null;
  location: string;
  status: "SCHEDULED" | "ONGOING" | "COMPLETED";
  minEloRating: number;
  maxEloRating: number;
  format: "SWISS" | "DOUBLE_ELIMINATION";
  roundsCompleted: number | null;
  currentRoundNumber: number;
  totalSwissRounds: number;
  winnersBracket: any[];
  losersBracket: any[];
  matches: Match[];
  adminId: number | null;
  adminUsername: string | null;
  players: Player[];
}

interface Match {
  id: number;
  durationInMinutes: number | null;
  status:
    | "SCHEDULED"
    | "COMPLETED"
    | "PENDING"
    | "CANCELLED"
    | "BYE"
    | "WAITING";
  bracket: "UPPER" | "LOWER" | "FINAL" | "GRAND_FINAL";
  player1Score: number | null;
  player2Score: number | null;
  matchDate: string;
  player1: Player;
  player2: Player;
  tournament: TournamentDetails;
  round?: number | null;
  winner?: Player | null;
}

interface PlayerInMatch {
  id: number;
  username: string;
  name: string;
  points: number;
}

interface Player {
  id: number;
  username: string;
  userType?: "ROLE_PLAYER" | "ROLE_ADMIN";
  name: string;
}

interface TournamentDetails {
  id: number;
  name: string;
  startDate: string;
  endDate: string | null;
  location: string;
  status: "SCHEDULED" | "COMPLETED" | "ONGOING";
  minEloRating: number;
  maxEloRating: number;
  format: "SWISS" | "DOUBLE_ELIMINATION";
}

interface PlayerLeaderBoard {
  id: number;
  username: string;
  userType?: "ROLE_PLAYER" | "ROLE_ADMIN";
  name: string;
  eloRating: number;
}
