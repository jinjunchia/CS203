"use client";

// components/ProtectedRoute.tsx
import React, { useContext, useEffect, ComponentType } from "react";
import { useRouter } from "next/navigation";
import { AuthContext } from "@/app/(context)/AuthContext";
import useAuth from "@/hooks/useAuth";

// Define the props for the HOC
type WithProtectedRouteProps = {
	// You can extend this with additional props if necessary
};

// Create the ProtectedRoute HOC
function ProtectedRoute<P extends WithProtectedRouteProps>(
	WrappedComponent: ComponentType<P>
): React.FC<P> {
	const ComponentWithAuth: React.FC<P> = (props) => {
		const { user } = useContext<AuthContextType>(AuthContext);
		const router = useRouter();

		useEffect(() => {
			if (!user) {
				console.log("JDBFIBFDBFB " + user);
				router.push("/login");
			}
		}, [user, router]);

		return <WrappedComponent {...props} />;
	};

	// Optional: Set a display name for easier debugging
	const wrappedComponentName =
		WrappedComponent.displayName || WrappedComponent.name || "Component";
	ComponentWithAuth.displayName = `ProtectedRoute(${wrappedComponentName})`;

	return ComponentWithAuth;
}

export default ProtectedRoute;
