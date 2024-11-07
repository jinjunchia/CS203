"use client";

import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

const Page = () => {
  const { data: session, status } = useSession();
  const router = useRouter();

  useEffect(() => {
    if ((session as any)?.user.user.userType === "ROLE_ADMIN") {
      router.replace("/dashboard/admin");
    } else {
      router.replace("/dashboard/player");
    }
  }, [router]);

  return null;
};

export default Page;
